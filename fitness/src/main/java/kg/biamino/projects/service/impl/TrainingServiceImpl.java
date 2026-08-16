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
import kg.biamino.projects.service.WorkloadServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final WorkloadServiceClient workloadServiceClient;


    @Autowired
    public TrainingServiceImpl( TrainingMapper trainingMapper, TrainingRepository trainingRepository, WorkloadServiceClient workloadServiceClient) {
        this.trainingMapper = trainingMapper;
        this.trainingRepository = trainingRepository;
        this.workloadServiceClient = workloadServiceClient;
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
        validationInput(trainingDto);
        log.info("Creating training {}", trainingDto);
        Training training = trainingMapper.toEntity(trainingDto);

        User trainer = training.getTrainer().getUser();

        TrainerWorkloadRequest trainerWorkloadRequest = new TrainerWorkloadRequest();
        trainerWorkloadRequest.setTrainingDate(training.getDate());
        trainerWorkloadRequest.setTrainingDuration(training.getDuration());
        trainerWorkloadRequest.setTrainerUsername(trainer.getUsername());
        trainerWorkloadRequest.setActive(trainer.getIsActive());
        trainerWorkloadRequest.setTrainerFirstName(trainer.getFirstName());
        trainerWorkloadRequest.setTrainerLastName(trainer.getLastName());
        trainerWorkloadRequest.setActionType(ActionType.ADD);

        workloadServiceClient.updateWorkload(trainerWorkloadRequest);

        return trainingRepository.save(training);
    }

    @Override
    @Transactional
    public void deleteTrainingById(Long id) {
        Training training = getTrainingById(id);
        if(training != null){
            trainingRepository.delete(training);

            User user = training.getTrainer().getUser();
            TrainerWorkloadRequest trainerWorkloadRequest = new TrainerWorkloadRequest();
            trainerWorkloadRequest.setTrainingDate(training.getDate());
            trainerWorkloadRequest.setTrainingDuration(training.getDuration());
            trainerWorkloadRequest.setTrainerLastName(user.getLastName());
            trainerWorkloadRequest.setTrainerUsername(user.getUsername());
            trainerWorkloadRequest.setActionType(ActionType.DELETE);
            trainerWorkloadRequest.setTrainerFirstName(user.getFirstName());
            trainerWorkloadRequest.setActive(user.getIsActive());
            workloadServiceClient.updateWorkload(trainerWorkloadRequest);
        }
    }


    private void validationInput(TrainingDto trainingDto) {
        nullChecker(trainingDto, "trainingDto");
        nullChecker(trainingDto.getTrainingName(), "trainingDto.trainingName");
        nullChecker(trainingDto.getTrainingType(), "trainingDto.trainingType");
        nullChecker(trainingDto.getTrainerUsername(), "trainingDto.trainerId");
        nullChecker(trainingDto.getTraineeUsername(), "trainingDto.traineeId");
        nullChecker(trainingDto.getTrainingStart(), "trainingDto.trainingStart");

        integerChecker(trainingDto.getDuration(), "trainingDto.duration");

        if(trainingDto.getTrainingName().isBlank()) {
            throw new IllegalArgumentException("TrainingDto name is blank");
        }
        if(trainingDto.getTrainingStart().isBefore(LocalDate.now())){
            throw new IllegalArgumentException("trainingStart start is cant be in the past");
        }



    }
}
