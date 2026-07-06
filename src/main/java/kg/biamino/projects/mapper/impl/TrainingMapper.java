package kg.biamino.projects.mapper.impl;

import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.mapper.Mapper;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.service.TraineeService;
import kg.biamino.projects.service.TrainerService;
import kg.biamino.projects.service.TrainingService;
import kg.biamino.projects.service.TrainingTypeService;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper implements Mapper<Training, TrainingDto> {

    private final TrainingTypeService trainingTypeService;
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    public TrainingMapper(TrainingTypeService trainingTypeService,
                          TrainerService trainerService, TraineeService traineeService) {
        this.trainingTypeService = trainingTypeService;
        this.trainerService = trainerService;
        this.traineeService = traineeService;
    }

    @Override
    public Training toEntity(TrainingDto trainingDto) {
        Training training = new Training();
        training.setTrainingName(trainingDto.getTrainingName());
        training.setTrainingType(trainingTypeService.findByName(trainingDto.getTrainingType()));
        training.setDate(trainingDto.getTrainingStart());
        training.setTrainer(trainerService.getTrainerById(trainingDto.getTrainerId()));
        training.setTrainee(traineeService.getTraineeById(trainingDto.getTraineeId()));
        training.setDuration(trainingDto.getDuration());

        return training;
    }

    @Override
    public TrainingDto toDto(Training training) {
        return null;
    }
}
