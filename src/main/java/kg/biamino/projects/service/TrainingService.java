package kg.biamino.projects.service;

import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.model.Training;

import java.util.List;

public interface TrainingService {
    Training getTrainingById(Long id);

    List<Training> getAllTrainings();

    Training createTraining(TrainingDto training);
}
