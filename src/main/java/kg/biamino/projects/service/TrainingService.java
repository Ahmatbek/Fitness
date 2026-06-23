package kg.biamino.projects.service;

import kg.biamino.projects.model.Training;

import java.util.List;

public interface TrainingService {
    Training getTrainingById(String name);

    List<Training> getAllTrainings();

    Training createTraining(String name, Training training);
}
