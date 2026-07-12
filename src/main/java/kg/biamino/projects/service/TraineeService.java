package kg.biamino.projects.service;

import kg.biamino.projects.dto.AuthUserDto;
import kg.biamino.projects.dto.TraineeDto;
import kg.biamino.projects.dto.TraineeStatusChangeDto;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.records.ProfilePasswordChange;
import kg.biamino.projects.records.TraineeCriteriaDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TraineeService {
    Trainee getTraineeById(Long id);

    List<Trainee> getAllTrainees();

    Trainee createTrainee(TraineeDto trainee);

    Trainee updateTrainee(AuthUserDto authUserDto, TraineeDto trainee);

    void deleteTraineeById(Long id);

    Trainee findByUsername(AuthUserDto authUserDto);

    Trainee passwordChange(ProfilePasswordChange profilePasswordChange);

    void changeStatusTrainee(TraineeStatusChangeDto traineeStatusChangeDto);

    @Transactional
    void removeTraineeByUsername(AuthUserDto authUserDto);

    List<Training> getTrainingsByCriteria(AuthUserDto authUserDto, TraineeCriteriaDto traineeCriteriaDto);

    List<Trainer> getTrainersNotAssignedToTrainee(AuthUserDto authUserDto);
}
