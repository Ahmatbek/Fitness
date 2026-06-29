package kg.biamino.projects.dao;

import kg.biamino.projects.model.Training;

import java.util.List;

public interface TrainingDao {

    Training getTraining(String name);

    List<Training> getAllTrainings();

    Training createTraining(String name, Training training);
}
