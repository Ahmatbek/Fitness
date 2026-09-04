package kg.biamino.projects.service;

import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.model.Training;


public interface TrainingService {
    Training getTrainingById(Long id);

    Training createTraining(TrainingDto training);

    void deleteTrainingById(Long id);

}
