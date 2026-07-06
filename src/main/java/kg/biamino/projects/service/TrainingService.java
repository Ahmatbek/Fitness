package kg.biamino.projects.service;

import kg.biamino.projects.dto.AuthUserDto;
import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.records.TraineeCriteriaDto;

import java.util.List;

public interface TrainingService {
    Training getTrainingById(Long id);

    List<Training> getAllTrainings();

    Training createTraining(AuthUserDto authUserDto, TrainingDto training);

    List<Training> findTrainingsByCriteria(Long traineeId, TraineeCriteriaDto traineeCriteriaDto);
}
