package kg.biamino.projects.service.impl;

import kg.biamino.projects.dto.AuthUserDto;
import kg.biamino.projects.dto.TraineeDto;
import kg.biamino.projects.dto.TraineeStatusChangeDto;
import kg.biamino.projects.dto.TrainerDto;
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
    public void setTraineeRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Autowired
    public void setTraineeDao(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public Trainee getTraineeById(Long id) {
        log.info("Getting trainee by id: {}", id);
        return traineeRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Trainee with id " + id + " not found"));

    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainee> getAllTrainees() {
        log.info("Getting all trainees");
        return traineeRepository.findAll();
    }

    @Override
    @Transactional
    public Trainee createTrainee(TraineeDto traineeDto) {
        dateValidation(traineeDto);
        log.info("Creating trainee: {}", traineeDto);
        User user = userService.createUser(traineeDto);


        Trainee trainee = new Trainee();
        trainee.setAddress(traineeDto.getAddress());
        trainee.setDateOfBirth(traineeDto.getDateOfBirth());
        trainee.setUser(user);


        return traineeRepository.save(trainee);
    }

    @Override
    public Trainee updateTrainee(AuthUserDto authUserDto, TraineeDto traineeDto) {
        userService.userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword());
        dateValidation(traineeDto);
        log.info("Updating trainee: {}", traineeDto);
        User user = userService.updateUser(authUserDto.getUsername(), traineeDto);

        Trainee trainee = traineeRepository.findById(user.getId()).orElse(null);
        nullChecker(trainee, "trainee");
        trainee.setDateOfBirth(traineeDto.getDateOfBirth());
        trainee.setAddress(traineeDto.getAddress() != null ? traineeDto.getAddress() : trainee.getAddress());

        return traineeRepository.update(trainee);

    }

    @Override
    @Transactional
    public void deleteTraineeById(Long id) {
        traineeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Trainee findByUsername(AuthUserDto authUserDto) {
        userService.userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword());
        stringChecker(authUserDto.getUsername(), "username");
        User user = userService.findUserByUsername(authUserDto.getUsername());
        return traineeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("Invalid username"));

    }


    @Override
    @Transactional
    public Trainee passwordChange(ProfilePasswordChange profilePasswordChange) {
        userService.userAuthenticated(profilePasswordChange.username(), profilePasswordChange.oldPassword());
        User user = userService.findUserByUsername(profilePasswordChange.username());
        userService.changePassword(user, profilePasswordChange.newPassword());
        return traineeRepository.findByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("user doesnt have trainee profile"));
    }

    @Override
    public void changeStatusTrainee(TraineeStatusChangeDto traineeStatusChangeDto) {
        AuthUserDto authUserDto = traineeStatusChangeDto.authUser();
        userService.userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword());
        User user = userService.findUserByUsername(authUserDto.getUsername());
        traineeRepository.findByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("User doesnt have trainee profile"));
        userService.changeStatus(user, traineeStatusChangeDto.status());
    }


    private void dateValidation(TraineeDto traineeDto) {
        if (traineeDto == null || traineeDto.getDateOfBirth() == null || traineeDto.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid date of birth");
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
        Trainee trainee = traineeRepository.findByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("user doesnt have trainee profile"));
        nullChecker(trainee, "trainee");
        return trainingRepository.findByCriteria(trainee.getId(), traineeCriteriaDto);

    }

    @Override
    public List<Trainer> getTrainersNotAssignedToTrainee(AuthUserDto authUserDto) {
        userService.userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword());
        User user = userService.findUserByUsername(authUserDto.getUsername());
        Trainee trainee = traineeRepository.findByUsername(user.getUsername()).orElseThrow(() -> new NoSuchElementException("user doesnt have trainee profile"));
        nullChecker(trainee, "trainee");
        return trainerRepository.findNotAssignedTrainees(trainee.getId());
    }




}
