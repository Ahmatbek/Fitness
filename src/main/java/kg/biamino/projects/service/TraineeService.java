package kg.biamino.projects.service;

import kg.biamino.projects.model.Trainee;

import java.util.List;

public interface TraineeService {
    Trainee getTraineeById(Long id);

    List<Trainee> getAllTrainees();

    Trainee createTrainee(Long id, Trainee trainee);

    Trainee updateTrainee(Long id, Trainee trainee);
}
