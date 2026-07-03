package kg.biamino.projects.service.impl;

import kg.biamino.projects.dto.AuthUserDto;
import kg.biamino.projects.dto.TraineeDto;
import kg.biamino.projects.dto.TrainerDto;
import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.records.ProfilePasswordChange;
import kg.biamino.projects.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.List;

@Service
@Slf4j
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
    @Transactional
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
        return trainerService.getTrainerById(userId);
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
    @Transactional
    public Trainee createTrainee(TraineeDto traineeDto){
        return traineeService.createTrainee(traineeDto);
    }

    @Override
    public void deleteTraineeById(Long traineeId){
        traineeService.deleteTraineeById(traineeId);
    }

    @Override
    public Training getTrainingById(Long id){
        return trainingService.getTrainingById(id);
    }

    @Override
    public List<Training> getAllTrainings(){
        return trainingService.getAllTrainings();
    }

    @Override
    public Training createTraining(TrainingDto trainingDto){
        return trainingService.createTraining(trainingDto);
    }

    @Override
    public Trainee getTraineeByUsername(AuthUserDto authUserDto){
        try{
            return traineeService.findByUsername(authUserDto);
        }catch (AuthenticationException e){
            log.error("Authentication failed {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Trainer getTrainerByUsername(AuthUserDto authUserDto){
        try{
            return trainerService.findByUsername(authUserDto);
        }catch (AuthenticationException e){
            log.error("Authentication failed {}",e.getMessage());
            throw new RuntimeException("Authentication failed"+e);
        }
    }

    @Override
    public Trainer passwordChangeTrainer(ProfilePasswordChange profilePasswordChange) {
       try{
           return trainerService.passwordChange(profilePasswordChange);
       }catch (AuthenticationException e){
           log.error("Authentication failed {}",e.getMessage());
           throw new RuntimeException("Authentication failed"+e);
       }
    }

    @Override
    public Trainee passwordChangeTrainee(ProfilePasswordChange profilePasswordChange) {
        try{
            return traineeService.passwordChange(profilePasswordChange);
        }catch (AuthenticationException e){
            log.error("Authentication failed {}",e.getMessage());
            throw new RuntimeException("Authentication failed"+e);
        }
    }
}
