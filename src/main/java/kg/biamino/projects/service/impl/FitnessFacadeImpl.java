package kg.biamino.projects.service.impl;

import kg.biamino.projects.dto.*;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.records.ProfilePasswordChange;
import kg.biamino.projects.records.TraineeCriteriaDto;
import kg.biamino.projects.records.TrainerCriteriaDto;
import kg.biamino.projects.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class FitnessFacadeImpl implements FitnessFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;

    @Autowired
    public FitnessFacadeImpl(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService, TrainingTypeService trainingTypeService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.trainingTypeService = trainingTypeService;
    }

    @Override
    @Transactional
    public Trainer createTrainer(TrainerDto trainerDto) {
        return trainerService.createTrainer(trainerDto);
    }

    @Override
    public Trainer updateTrainer(AuthUserDto authUserDto, TrainerDto trainerDto) {
        return trainerService.updateTrainer(authUserDto, trainerDto);

    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerService.getAllTrainers();
    }

    @Override
    public Trainer getTrainerByUserId(Long userId) {
        return trainerService.getTrainerById(userId);
    }

    @Override
    public Trainee getTraineeByUserId(Long userId) {
        return traineeService.getTraineeById(userId);
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return traineeService.getAllTrainees();
    }

    @Override
    public Trainee updateTrainee(AuthUserDto authUserDto, TraineeDto traineeDto) {
        return traineeService.updateTrainee(authUserDto, traineeDto);

    }

    @Override
    @Transactional
    public Trainee createTrainee(TraineeDto traineeDto) {
        return traineeService.createTrainee(traineeDto);
    }

    @Override
    public void deleteTraineeById(Long traineeId) {
        traineeService.deleteTraineeById(traineeId);
    }

    @Override
    public Training getTrainingById(Long id) {
        return trainingService.getTrainingById(id);
    }

    @Override
    public List<Training> getAllTrainings() {
        return trainingService.getAllTrainings();
    }

    @Override
    public Training createTraining(AuthUserDto authUserDto,TrainingDto trainingDto) {
        return trainingService.createTraining(authUserDto,trainingDto);
    }

    @Override
    public Trainee getTraineeByUsername(AuthUserDto authUserDto) {
        return traineeService.findByUsername(authUserDto);

    }

    @Override
    public Trainer getTrainerByUsername(AuthUserDto authUserDto) {
        return trainerService.findByUsername(authUserDto);

    }

    @Override
    public Trainer passwordChangeTrainer(ProfilePasswordChange profilePasswordChange) {
        return trainerService.passwordChange(profilePasswordChange);

    }

    @Override
    public Trainee passwordChangeTrainee(ProfilePasswordChange profilePasswordChange) {
        return traineeService.passwordChange(profilePasswordChange);

    }

    @Override
    public void changeStatusTrainee(TraineeStatusChangeDto traineeStatusChangeDto) {
        traineeService.changeStatusTrainee(traineeStatusChangeDto);
    }

    @Override
    public void changeStatusTrainer(TraineeStatusChangeDto traineeStatusChangeDto) {
        trainerService.changeStatusTrainer(traineeStatusChangeDto);
    }

    @Override
    public TrainingType findTrainingTypeByName(String name){
        return trainingTypeService.findByName(name);
    }

    @Override
    public List<Training> getTrainingsByTraineeUsernameAndCriteria(AuthUserDto authUserDto, TraineeCriteriaDto traineeCriteriaDto) {
        return traineeService.getTrainingsByCriteria(authUserDto, traineeCriteriaDto);
    }

    @Override
    public List<Training> getTrainingsByTrainerUsernameAndCriteria(AuthUserDto authUserDto, TrainerCriteriaDto traineeCriteriaDto) {
        return trainerService.getTrainingsByCriteria(authUserDto, traineeCriteriaDto);
    }

    @Override
    public List<Trainer> getTrainersNotAssignedToTrainee(AuthUserDto authUserDto) {
        return traineeService.getTrainersNotAssignedToTrainee(authUserDto);
    }

    @Override
    public List<Trainer> updateTraineeTrainersList(AuthUserDto authUserDto, List<TrainerDto> trainerDtos) {
        return trainerService.updateTraineeTrainersList(authUserDto, trainerDtos);
    }
}
