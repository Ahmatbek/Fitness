package kg.biamino.projects.service;

import kg.biamino.projects.dto.*;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.records.ProfilePasswordChange;
import kg.biamino.projects.records.TraineeCriteriaDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TraineeService {
    Trainee getTraineeById(Long id);

//    List<TraineeDto> getAllTrainees();

    UserCredentialsDto createTrainee(TraineeDto trainee);

    TraineeTrainersListDto updateTrainee(UpdateTraineeDto trainee, String username);

    void deleteTraineeById(Long id);

    TraineeTrainersListDto findByUsername(String authUserDto);

    Trainee passwordChange(ProfilePasswordChange profilePasswordChange);

    void changeStatusTrainee(ProfileStatusChangeDto profileStatusChangeDto);

    @Transactional
    void removeTraineeByUsername(AuthUserDto authUserDto);

    List<Training> getTrainingsByCriteria(AuthUserDto authUserDto, TraineeCriteriaDto traineeCriteriaDto);

    List<Trainer> getTrainersNotAssignedToTrainee(AuthUserDto authUserDto);

    void deleteTraineeByUsername(String username, String authUsername);
}
