package kg.biamino.projects.dao;

import kg.biamino.projects.model.Trainee;

import java.util.List;

public interface TraineeDao {
    Trainee getTrainee(String userId);

    Trainee updateTrainee(String userId, Trainee trainee);

    void deleteTrainee(String userId);

    Trainee createTrainee(String userId, Trainee trainee);

    List<Trainee> getAllTrainees();
}
