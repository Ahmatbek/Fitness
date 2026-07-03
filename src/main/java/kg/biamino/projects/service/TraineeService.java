package kg.biamino.projects.service;

import kg.biamino.projects.dto.AuthUserDto;
import kg.biamino.projects.dto.TraineeDto;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.records.ProfilePasswordChange;

import javax.naming.AuthenticationException;
import java.util.List;

public interface TraineeService {
    Trainee getTraineeById(Long id);

    List<Trainee> getAllTrainees();

    Trainee createTrainee(TraineeDto trainee);

    Trainee updateTrainee(String username, TraineeDto trainee);

    void deleteTraineeById(Long id);

    Trainee findByUsername(AuthUserDto authUserDto) throws AuthenticationException;

    Trainee passwordChange(ProfilePasswordChange profilePasswordChange) throws AuthenticationException;
}
