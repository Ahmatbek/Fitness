package kg.biamino.projects.service.impl;

import kg.biamino.projects.auth.AuthHandler;
import kg.biamino.projects.dto.*;
import kg.biamino.projects.exception.AuthorizationException;
import kg.biamino.projects.exception.DateInvalidException;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.User;
import kg.biamino.projects.repository.TraineeRepository;
import kg.biamino.projects.repository.TrainerRepository;
import kg.biamino.projects.repository.TrainingRepository;
import kg.biamino.projects.service.TraineeService;
import kg.biamino.projects.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static kg.biamino.projects.utils.ValidationInput.nullChecker;
import static kg.biamino.projects.utils.ValidationInput.stringChecker;


@Service
@Slf4j
public class TraineeServiceImpl implements TraineeService {

    private TraineeRepository traineeRepository;
    private TrainerRepository trainerRepository;
    private final UserService userService;
    private TrainingRepository trainingRepository;

    @Autowired
    public TraineeServiceImpl(UserService userService){
        this.userService = userService;
    }

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }


    @Override
    public Trainee getTraineeByUsername(String name) {
        User user = userService.findUserByUsername(name);
        return traineeRepository.findByUserId(user.getId()).orElseThrow(()-> new NoSuchElementException("user doesnt exist"));
    }

    @Override
    @Transactional
    public UserCredentialsDto createTrainee(TraineeDto traineeDto) {
        nullChecker(traineeDto, "traineeDto");
        dateValidation(traineeDto.getDateOfBirth());
        log.info("Creating trainee: {}", traineeDto);
        User user = userService.createUser(traineeDto);


        Trainee trainee = new Trainee();
        trainee.setAddress(traineeDto.getAddress());
        trainee.setDateOfBirth(traineeDto.getDateOfBirth());
        trainee.setUser(user);


        traineeRepository.save(trainee);

        return UserCredentialsDto.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();

    }

    @Override
    @Transactional
    public TraineeTrainersListDto updateTrainee(UpdateTraineeDto traineeDto, String username) {
        nullChecker(traineeDto, "UpdateTraineeDto");
        dateValidation(traineeDto.getDateOfBirth());
        log.info("Updating trainee: {}", traineeDto);
        if(!traineeDto.getUsername().equals(username)) {
            throw new AuthorizationException("You dont have access to change others accounts");
        }
        User user = userService.updateUser(username, traineeDto, traineeDto.getIsActive());

        Trainee trainee = traineeRepository.findByUserIdToGetTrainers(user.getId()).orElseThrow(()-> new NoSuchElementException("user doesnt have trainee profile"));
        nullChecker(trainee, "trainee");
        trainee.setDateOfBirth(traineeDto.getDateOfBirth()!=null ? traineeDto.getDateOfBirth() : trainee.getDateOfBirth());
        trainee.setAddress(traineeDto.getAddress() != null ? traineeDto.getAddress() : trainee.getAddress());

        traineeRepository.update(trainee);

        return TraineeTrainersListDto.builder()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .address(trainee.getAddress())
                .isActive(user.getIsActive())
                .dateOfBirth(trainee.getDateOfBirth())
                .trainers(trainee.getTrainers().stream().map(this::toTrainerUsernameDto).toList())
                .build();


    }

    @Transactional(readOnly = true)
    @Override
    public TraineeTrainersListDto findByUsername(String username) {
        stringChecker(username, "username");
        User user = userService.findUserByUsername(username);
        Trainee tr =  traineeRepository.findByUserIdToGetTrainers(user.getId())
                .orElseThrow(()-> new NoSuchElementException("user doesnt have trainee profile"));

        return TraineeTrainersListDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .address(tr.getAddress())
                .dateOfBirth(tr.getDateOfBirth())
                .isActive(user.getIsActive())
                .trainers(tr.getTrainers().stream().map(this::toTrainerUsernameDto).toList())
                .build();

    }



    @Override
    public void changeStatusTrainer(ChangeStatusDto changeStatusDto, String authUsername) {
        AuthHandler.checkAuthorization(changeStatusDto.getUsername(), authUsername);
        User user = userService.findUserByUsername(authUsername);
        traineeRepository.findByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("User doesnt have trainee profile"));
        userService.changeStatus(user, changeStatusDto.getIsActive());
    }



    private void dateValidation(LocalDate traineeDto) {
        if (traineeDto != null && traineeDto.isAfter(LocalDate.now())) {
            throw new DateInvalidException("Invalid date of birth");
        }
    }


    @Override
    @Transactional
    public List<TrainingsDisplayInfoTrainee> getTrainingsByCriteria(TraineeTrainingsDto traineeCriteriaDto) {
        List<Training> trainings = trainingRepository.findByCriteria(traineeCriteriaDto.getUsername(), traineeCriteriaDto);
        return trainings.stream().map(this::toTrainingsDisplayInfoTrainee).toList();

    }


    @Override
    @Transactional
    public void deleteTraineeByUsername(String username, String authUsername) {
        AuthHandler.checkAuthorization(username, authUsername);
        User user  = userService.findUserByUsername(username);
        Trainee trainee = traineeRepository.findByUserIdToGetTrainers(user.getId()).orElseThrow(()-> new NoSuchElementException("user doesnt have trainee profile"));
        traineeRepository.deleteById(trainee.getId());
    }

    @Override
    public List<TrainerUsernameDto> findNotAssignedTrainersByUsername(String username) {
        nullChecker(username, "username");
        User user = userService.findUserByUsername(username);
        Trainee trainee = traineeRepository.findByUsername(user.getUsername()).orElseThrow(()-> new NoSuchElementException("user doesnt have trainee profile"));

        List<Trainer> trainers = trainerRepository.findNotAssignedTrainersByTraineeId(trainee.getId());

        return trainers.stream().map(this::toTrainerUsernameDto).toList();



    }

    @Override
    @Transactional
    public List<TrainerUsernameDto> updateTrainersByUsername(UpdateTraineeTrainersDto updateTraineeTrainersDto, String username) {
        AuthHandler.checkAuthorization(updateTraineeTrainersDto.getUsername(), username);
        Trainee trainee = traineeRepository.findByUsername(username).orElseThrow(()-> new NoSuchElementException("user doesnt have trainee profile"));
        List<Trainer> trainers = trainee.getTrainers();
        List<String> usernames = updateTraineeTrainersDto.getTrainers();
        List<Trainer> newTrainers = new ArrayList<>();

        for (Trainer trainer : trainers) {
            if(!usernames.contains(trainer.getUser().getUsername())) {
                trainer.getTrainees().remove(trainee);
            }
        }

        for(String userName : usernames) {
            User user = userService.findUserByUsername(userName);
            Trainer trainer1 = trainerRepository.findByUserId(user.getId()).orElseThrow(()-> new NoSuchElementException("trainer doesnt have trainer profile"));
            newTrainers.add(trainer1);
            if(!trainer1.getTrainees().contains(trainee)) {
                trainer1.getTrainees().add(trainee);
            }
        }

        return newTrainers.stream().map(this::toTrainerUsernameDto).toList();


    }

    private TrainerUsernameDto toTrainerUsernameDto(Trainer trainer) {
        return TrainerUsernameDto.builder()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .username(trainer.getUser().getUsername())
                .specialization(trainer.getSpecialization().getName())
                .build();
    }



    private TrainingsDisplayInfoTrainee toTrainingsDisplayInfoTrainee (Training training){
        return TrainingsDisplayInfoTrainee.builder()
                .trainerName(training.getTrainee().getUser().getFirstName())
                .trainingDate(training.getDate())
                .trainingDuration(training.getDuration())
                .trainingName(training.getTrainingName())
                .trainingType(training.getTrainingType().getName())
                .build();
    }
}
