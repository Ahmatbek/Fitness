package kg.biamino.projects.service;

import kg.biamino.projects.dto.*;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.records.ProfilePasswordChange;
import kg.biamino.projects.records.TraineeCriteriaDto;
import kg.biamino.projects.records.TrainerCriteriaDto;

import java.util.List;

public interface FitnessFacade {

    Trainer createTrainer(TrainerDto trainerDto);

    Trainer updateTrainer(AuthUserDto authUserDto, TrainerDto trainerDto);

    List<Trainer> getAllTrainers();

    Trainer getTrainerByUserId(Long userId);

    Trainee getTraineeByUserId(Long userId);

    List<Trainee> getAllTrainees();

    Trainee updateTrainee(AuthUserDto authUserDto, TraineeDto traineeDto);

    Trainee createTrainee(TraineeDto traineeDto);

    void deleteTraineeById(Long traineeId);

    Training getTrainingById(Long id);

    List<Training> getAllTrainings();

    Training createTraining(AuthUserDto authUserDto,TrainingDto trainingDto);

    Trainee getTraineeByUsername(AuthUserDto authUserDto);

    Trainer getTrainerByUsername(AuthUserDto authUserDto);

    Trainer passwordChangeTrainer(ProfilePasswordChange profilePasswordChange);

    Trainee passwordChangeTrainee(ProfilePasswordChange profilePasswordChange);

    void changeStatusTrainee(TraineeStatusChangeDto traineeStatusChangeDto);

    void changeStatusTrainer(TraineeStatusChangeDto traineeStatusChangeDto);

    TrainingType findTrainingTypeByName(String name);

    List<Training> getTrainingsByTraineeUsernameAndCriteria(AuthUserDto authUserDto, TraineeCriteriaDto traineeCriteriaDto);

    List<Training> getTrainingsByTrainerUsernameAndCriteria(AuthUserDto authUserDto, TrainerCriteriaDto traineeCriteriaDto);
}
