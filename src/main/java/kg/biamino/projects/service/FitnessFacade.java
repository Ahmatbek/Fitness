package kg.biamino.projects.service;

import kg.biamino.projects.dto.AuthUserDto;
import kg.biamino.projects.dto.TraineeDto;
import kg.biamino.projects.dto.TrainerDto;
import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.records.ProfilePasswordChange;

import java.util.List;

public interface FitnessFacade {

    Trainer createTrainer(TrainerDto trainerDto);

    Trainer updateTrainer(String username, TrainerDto trainerDto);

    List<Trainer> getAllTrainers();

    Trainer getTrainerByUserId(Long userId);

    Trainee getTraineeByUserId(Long userId);

    List<Trainee> getAllTrainees();

    Trainee updateTrainee(String username, TraineeDto traineeDto);

    Trainee createTrainee(TraineeDto traineeDto);

    void deleteTraineeById(Long traineeId);

    Training getTrainingById(Long id);

    List<Training> getAllTrainings();

    Training createTraining(TrainingDto trainingDto);

    Trainee getTraineeByUsername(AuthUserDto authUserDto);

    Trainer getTrainerByUsername(AuthUserDto authUserDto);

    Trainer passwordChangeTrainer(ProfilePasswordChange profilePasswordChange);

    Trainee passwordChangeTrainee(ProfilePasswordChange profilePasswordChange);
}
