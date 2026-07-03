package kg.biamino.projects.service.impl;

import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.repository.TrainingRepository;
import kg.biamino.projects.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static kg.biamino.projects.utils.ValidationInput.integerChecker;
import static kg.biamino.projects.utils.ValidationInput.nullChecker;

@Service
@Slf4j
public class TrainingServiceImpl implements TrainingService {
    private TrainingRepository trainingRepository;

    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Override
    public Training getTrainingById(Long id) {
        log.info("Getting training by id {}", id);
        return trainingRepository.findById(id).orElseThrow(()-> new NoSuchElementException("Training with id " + id + " not found"));
    }

    @Override
    public List<Training> getAllTrainings() {
        log.info("Getting all trainings");
        return trainingRepository.findAll();
    }

    @Override
    public Training createTraining(TrainingDto trainingDto){
        validationInput(trainingDto);
        log.info("Creating training {}", trainingDto);

        Training training = builder(trainingDto);
        return trainingRepository.save(training);
    }

    private Training builder(TrainingDto trainingDto) {
        Training training = new Training();
        training.setTrainingName(trainingDto.getTrainingName());
        training.setTrainingType(trainingDto.getTrainingType());
        training.setDate(trainingDto.getTrainingStart());
        training.setTrainer(new Trainer());
        training.setTrainee(new Trainee());
        training.setDuration(trainingDto.getDuration());
        return training;
    }

    private void validationInput(TrainingDto trainingDto) {
        nullChecker(trainingDto, "trainingDto");
        nullChecker(trainingDto.getTrainingName(), "trainingDto.trainingName");
        nullChecker(trainingDto.getTrainingType(), "trainingDto.trainingType");
        nullChecker(trainingDto.getTrainerId(), "trainingDto.trainerId");
        nullChecker(trainingDto.getTraineeId(), "trainingDto.traineeId");
        nullChecker(trainingDto.getTrainingStart(), "trainingDto.trainingStart");

        integerChecker(trainingDto.getTrainerId(), "trainerId is 0 or less");
        integerChecker(trainingDto.getTraineeId(), "traineeId is 0 or less");
        integerChecker(trainingDto.getDuration(), "trainingDto.duration");

        if(trainingDto.getTrainingName().isBlank()) {
            throw new IllegalArgumentException("TrainingDto name is blank");
        }
        if(trainingDto.getTrainingStart().isBefore(LocalDate.now())){
            throw new IllegalArgumentException("trainingStart start is cant be in the past");
        }



    }
}
