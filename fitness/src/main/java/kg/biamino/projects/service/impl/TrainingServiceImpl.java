package kg.biamino.projects.service.impl;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import kg.biamino.projects.enums.ActionType;
import kg.biamino.projects.dto.TrainerWorkloadRequest;
import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.mapper.impl.TrainingMapper;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.User;
import kg.biamino.projects.repository.TrainingRepository;
import kg.biamino.projects.service.TrainingService;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static kg.biamino.projects.utils.ValidationInput.integerChecker;
import static kg.biamino.projects.utils.ValidationInput.nullChecker;

@Service
@Slf4j
@Counted(value = "training.methods", description = "userService number of times each method is called")
@Timed(value = "training", description = "amount of time each method executes")
public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepository trainingRepository;
    private final TrainingMapper trainingMapper;
    private final JmsTemplate jmsTemplate;


    @Autowired
    public TrainingServiceImpl( TrainingMapper trainingMapper, TrainingRepository trainingRepository, JmsTemplate jmsTemplate) {
        this.trainingMapper = trainingMapper;
        this.trainingRepository = trainingRepository;
        this.jmsTemplate = jmsTemplate;
    }



    @Override
    @Transactional(readOnly = true)
    public Training getTrainingById(Long id) {
        log.info("Getting training by id {}", id);
        return trainingRepository.findById(id).orElseThrow(()-> new NoSuchElementException("Training with id " + id + " not found"));
    }

    @Override
    @Transactional
    public Training createTraining( TrainingDto trainingDto){
        log.info("Creating training {}", trainingDto);
        Training training = trainingMapper.toEntity(trainingDto);

        User trainer = training.getTrainer().getUser();

        TrainerWorkloadRequest trainerWorkloadRequest = createTrainerWorkloadRequest(training, trainer, ActionType.ADD);
        jmsTemplate.convertAndSend("training-queue", trainerWorkloadRequest, message -> {
            message.setStringProperty("transactionId", MDC.get("transactionId"));
            return message;
        });
        return trainingRepository.save(training);
    }

    private static TrainerWorkloadRequest createTrainerWorkloadRequest(Training training, User trainer, ActionType actionType) {
        TrainerWorkloadRequest trainerWorkloadRequest = new TrainerWorkloadRequest();
        trainerWorkloadRequest.setTrainingDate(training.getDate());
        trainerWorkloadRequest.setTrainingDuration(training.getDuration());
        trainerWorkloadRequest.setTrainerUsername(trainer.getUsername());
        trainerWorkloadRequest.setActive(trainer.getIsActive());
        trainerWorkloadRequest.setTrainerFirstName(trainer.getFirstName());
        trainerWorkloadRequest.setTrainerLastName(trainer.getLastName());
        trainerWorkloadRequest.setActionType(actionType);
        return trainerWorkloadRequest;
    }

    @Override
    @Transactional
    public void deleteTrainingById(Long id) {
        Training training = getTrainingById(id);
        if(training != null){

            User user = training.getTrainer().getUser();
            TrainerWorkloadRequest trainerWorkloadRequest = createTrainerWorkloadRequest(training, user, ActionType.DELETE);

            jmsTemplate.convertAndSend("training-queue", trainerWorkloadRequest);
            trainingRepository.delete(training);
        }
    }


}
