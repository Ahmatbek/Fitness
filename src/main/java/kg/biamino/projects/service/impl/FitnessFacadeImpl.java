package kg.biamino.projects.service.impl;

import kg.biamino.projects.dto.TraineeDto;
import kg.biamino.projects.dto.TrainerDto;
import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FitnessFacadeImpl implements FitnessFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public FitnessFacadeImpl(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    @Override
    public Trainer createTrainer(TrainerDto trainerDto){
       return trainerService.createTrainer(trainerDto);
    }

    @Override
    public Trainer updateTrainer(String username, TrainerDto trainerDto){
        return trainerService.updateTrainer(username,trainerDto);
    }

    @Override
    public List<Trainer> getAllTrainers(){
        return trainerService.getAllTrainers();
    }
    @Override
    public Trainer getTrainerByUserId(Long userId){
        return trainerService.getTrainer(userId);
    }

    @Override
    public Trainee getTraineeByUserId(Long userId){
        return traineeService.getTraineeById(userId);
    }

    @Override
    public List<Trainee> getAllTrainees(){
        return traineeService.getAllTrainees();
    }

    @Override
    public Trainee updateTrainee(String username, TraineeDto traineeDto){
        return traineeService.updateTrainee(username,traineeDto);
    }

    @Override
    public Trainee createTrainee(TraineeDto traineeDto){
        return traineeService.createTrainee(traineeDto);
    }

    @Override
    public void deleteTraineeByUsername(String username){
        traineeService.deleteTrainee(username);
    }

    @Override
    public Training getTrainingByName(String name){
        return trainingService.getTrainingByName(name);
    }

    @Override
    public List<Training> getAllTrainings(){
        return trainingService.getAllTrainings();
    }

    @Override
    public Training createTraining(TrainingDto trainingDto){
        return trainingService.createTraining(trainingDto);
    }
}
