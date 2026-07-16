package kg.biamino.projects.service.impl;

import kg.biamino.projects.auth.AuthHandler;
import kg.biamino.projects.dto.*;
import kg.biamino.projects.exception.AuthorizationException;
import kg.biamino.projects.exception.DateInvalidException;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.User;
import kg.biamino.projects.records.ProfilePasswordChange;
import kg.biamino.projects.records.TraineeCriteriaDto;
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
    @Transactional(readOnly = true)
    public Trainee getTraineeById(Long id) {
        log.info("Getting trainee by id: {}", id);
        return traineeRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Trainee with id " + id + " not found"));

    }



    @Override
    @Transactional
    public UserCredentialsDto createTrainee(TraineeDto traineeDto) {
//        dateValidation(traineeDto);
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
        dateValidation(traineeDto);
        log.info("Updating trainee: {}", traineeDto);
        if(!traineeDto.getUsername().equals(username)) {
            throw new AuthorizationException("You dont have access to change others accounts");
        }
        User user = userService.updateUser(username, traineeDto);

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

    @Override
    @Transactional
    public void deleteTraineeById(Long id) {
        traineeRepository.deleteById(id);
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
    @Transactional
    public Trainee passwordChange(ProfilePasswordChange profilePasswordChange) {
        userService.userAuthenticated(profilePasswordChange.username(), profilePasswordChange.oldPassword());
        User user = userService.findUserByUsername(profilePasswordChange.username());
        stringChecker(profilePasswordChange.newPassword(), "new password");
        userService.changePassword(user, profilePasswordChange.newPassword());
        return traineeRepository.findByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("user doesnt have trainee profile"));
    }

    @Override
    public void changeStatusTrainee(ProfileStatusChangeDto profileStatusChangeDto) {
        AuthUserDto authUserDto = profileStatusChangeDto.authUser();
        userService.userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword());
        User user = userService.findUserByUsername(authUserDto.getUsername());
        traineeRepository.findByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("User doesnt have trainee profile"));
        userService.changeStatus(user, profileStatusChangeDto.status());
    }


    private void dateValidation(UpdateTraineeDto traineeDto) {
        nullChecker(traineeDto, "traineeDto");
        if (traineeDto.getDateOfBirth() != null && traineeDto.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new DateInvalidException("Invalid date of birth");
        }
    }

    @Transactional
    @Override
    public void removeTraineeByUsername(AuthUserDto authUserDto) {
        userService.userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword());
        User user = userService.findUserByUsername(authUserDto.getUsername());
        Trainee trainee = traineeRepository.findByUserId(user.getId()).orElseThrow(()-> new NoSuchElementException("user doesnt have trainee profile"));
        nullChecker(trainee, "trainee");
        traineeRepository.deleteById(trainee.getId());
    }

    @Override
    @Transactional
    public List<Training> getTrainingsByCriteria(AuthUserDto authUserDto, TraineeCriteriaDto traineeCriteriaDto) {
        userService.userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword());
        User user = userService.findUserByUsername(authUserDto.getUsername());
        return trainingRepository.findByCriteria(user.getUsername(), traineeCriteriaDto);

    }

    @Override
    public List<Trainer> getTrainersNotAssignedToTrainee(AuthUserDto authUserDto) {
        userService.userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword());
        User user = userService.findUserByUsername(authUserDto.getUsername());
        Trainee trainee = traineeRepository.findByUsername(user.getUsername()).orElseThrow(() -> new NoSuchElementException("user doesnt have trainee profile"));
        nullChecker(trainee, "trainee");
        return trainerRepository.findNotAssignedTrainees(trainee.getId());
    }

    @Override
    @Transactional
    public void deleteTraineeByUsername(String username, String authUsername) {
        AuthHandler.checkAuthorization(username, authUsername);
        User user  = userService.findUserByUsername(username);
        Trainee trainee = traineeRepository.findByUserIdToGetTrainers(user.getId()).orElseThrow(()-> new NoSuchElementException("user doesnt have trainee profile"));

        traineeRepository.deleteById(trainee.getId());
    }

    private TrainerUsernameDto toTrainerUsernameDto(Trainer trainer) {
        return TrainerUsernameDto.builder()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .username(trainer.getUser().getUsername())
                .specialization(trainer.getSpecialization().getName())
                .build();
    }


}
