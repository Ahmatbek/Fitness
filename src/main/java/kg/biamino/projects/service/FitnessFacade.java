package kg.biamino.projects.service;

import kg.biamino.projects.dto.TraineeDto;
import kg.biamino.projects.dto.TrainerDto;
import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;

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

    void deleteTraineeByUsername(String username);

    Training getTrainingByName(String name);

    List<Training> getAllTrainings();

    Training createTraining(TrainingDto trainingDto);
}
