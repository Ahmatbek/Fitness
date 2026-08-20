package kg.biamino.projects.service;

import kg.biamino.projects.dto.*;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TraineeService {

    Trainee getTraineeByUsername(String name);

    UserCredentialsDto createTrainee(TraineeDto trainee);

    TraineeTrainersListDto updateTrainee(UpdateTraineeDto trainee, String username);

    TraineeTrainersListDto findByUsername(String authUserDto);

    void changeStatusTrainer(ChangeStatusDto changeStatusDto, String authUsername);

    List<TrainingsDisplayInfoTrainee> getTrainingsByCriteria(TraineeTrainingsDto trainee);

    void deleteTraineeByUsername(String username, String authUsername);

    List<TrainerUsernameDto> findNotAssignedTrainersByUsername(String username);

    List<TrainerUsernameDto>  updateTrainersByUsername(UpdateTraineeTrainersDto updateTraineeTrainersDto, String username);
}
