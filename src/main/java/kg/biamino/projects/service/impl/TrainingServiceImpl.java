package kg.biamino.projects.service.impl;

import kg.biamino.projects.dao.TrainingDao;
import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static kg.biamino.projects.utils.ValidationInput.integerChecker;
import static kg.biamino.projects.utils.ValidationInput.nullChecker;

@Service
@Slf4j
public class TrainingServiceImpl implements TrainingService {
    private TrainingDao trainingDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Override
    public Training getTrainingById(String name) {
        log.info("Getting training by name {}", name);
        return trainingDao.getTraining(name);
    }

    @Override
    public List<Training> getAllTrainings() {
        log.info("Getting all trainings");
        return trainingDao.getAllTrainings();
    }

    @Override
    public Training createTraining(TrainingDto trainingDto){
        validationInput(trainingDto);
        log.info("Creating training {}", trainingDto);

        Training training = builder(trainingDto);
        return trainingDao.createTraining(training.getTrainingName(),training);
    }

    private Training builder(TrainingDto trainingDto) {
        Training training = new Training();
        training.setTrainingName(trainingDto.getTrainingName());
        training.setTrainingType(trainingDto.getTrainingType());
        training.setTrainingStart(trainingDto.getTrainingStart());
        training.setTrainerId(trainingDto.getTrainerId());
        training.setTraineeId(trainingDto.getTraineeId());
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
            throw new IllegalArgumentException("TrainingDto is null");
        }

        if(trainingDto.getTrainingStart().isAfter(LocalDate.now())){
            throw new IllegalArgumentException("TrainingStart is null");
        }


    }
}
