package kg.biamino.projects.service.impl;

import kg.biamino.projects.dto.AuthUserDto;
import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.mapper.impl.TrainingMapper;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.records.TraineeCriteriaDto;
import kg.biamino.projects.repository.TrainingRepository;
import kg.biamino.projects.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static kg.biamino.projects.utils.ValidationInput.integerChecker;
import static kg.biamino.projects.utils.ValidationInput.nullChecker;

@Service
@Slf4j
public class TrainingServiceImpl implements TrainingService {
    private TrainingRepository trainingRepository;
    private final UserService userService;
    private final TrainingMapper trainingMapper;

    @Autowired
    public TrainingServiceImpl(UserService userService, TrainingMapper trainingMapper) {
        this.userService = userService;
        this.trainingMapper = trainingMapper;
    }

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
    @Transactional
    public Training createTraining(AuthUserDto authUserDto, TrainingDto trainingDto){
        userService.userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword());
        validationInput(trainingDto);
        log.info("Creating training {}", trainingDto);
        Training training = trainingMapper.toEntity(trainingDto);
        return trainingRepository.save(training);
    }


    @Override
    public List<Training> findTrainingsByCriteria(Long traineeId, TraineeCriteriaDto traineeCriteriaDto) {
       return   trainingRepository.findByCriteria(traineeId, traineeCriteriaDto);
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
