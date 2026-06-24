package kg.biamino.projects.service;

import kg.biamino.projects.dto.TraineeDto;
import kg.biamino.projects.model.Trainee;

import java.util.List;

public interface TraineeService {
    Trainee getTraineeById(Long id);

    List<Trainee> getAllTrainees();

    Trainee createTrainee(TraineeDto trainee);

    Trainee updateTrainee(String username, TraineeDto trainee);

    void deleteTrainee(String username);
}
