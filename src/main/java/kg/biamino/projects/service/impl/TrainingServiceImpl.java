package kg.biamino.projects.service.impl;

import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.mapper.impl.TrainingMapper;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.repository.TrainingRepository;
import kg.biamino.projects.service.*;
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
public class TrainingServiceImpl implements TrainingService {
    private TrainingRepository trainingRepository;
    private final TrainingMapper trainingMapper;

    @Autowired
    public TrainingServiceImpl( TrainingMapper trainingMapper) {
        this.trainingMapper = trainingMapper;
    }

    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
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
        return trainingRepository.save(training);
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
