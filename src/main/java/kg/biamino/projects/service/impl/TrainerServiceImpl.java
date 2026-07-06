package kg.biamino.projects.service.impl;

import kg.biamino.projects.dto.AuthUserDto;
import kg.biamino.projects.dto.TraineeStatusChangeDto;
import kg.biamino.projects.dto.TrainerDto;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.User;
import kg.biamino.projects.records.ProfilePasswordChange;
import kg.biamino.projects.records.TrainerCriteriaDto;
import kg.biamino.projects.repository.TraineeRepository;
import kg.biamino.projects.repository.TrainerRepository;
import kg.biamino.projects.repository.TrainingRepository;
import kg.biamino.projects.service.TrainerService;
import kg.biamino.projects.service.TrainingTypeService;
import kg.biamino.projects.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static kg.biamino.projects.utils.ValidationInput.nullChecker;
import static kg.biamino.projects.utils.ValidationInput.stringChecker;

@Service
@Slf4j
public class TrainerServiceImpl implements TrainerService {

    private TrainerRepository trainerRepository;
    private TrainingRepository trainingRepository;
    private final TrainingTypeService trainingTypeService;
    private TraineeRepository traineeRepository;
    private final UserService userService;

    @Autowired
    public  TrainerServiceImpl(UserService userService,  TrainingTypeService trainingTypeService) {
        this.userService = userService;
        this.trainingTypeService = trainingTypeService;
    }

    @Autowired
    public void setTrainerDao(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer getTrainerById(Long id) {
        log.info("Getting trainer with id {}", id);
        return trainerRepository.findById(id).orElseThrow(()-> new NoSuchElementException("Trainer with id " + id + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getAllTrainers() {
        log.info("Getting all trainers");
        return trainerRepository.findAll();
    }

    @Override
    @Transactional
    public Trainer createTrainer(TrainerDto trainerDto) {
        nullChecker(trainerDto, "trainerDto");
        User user = userService.createUser(trainerDto);

        nullChecker(trainerDto.getSpecialization(), "specialization");

        Trainer trainer = new Trainer();
        trainer.setSpecialization(trainingTypeService.findByName(trainerDto.getSpecialization()));
        trainer.setUser(user);

        log.info("Creating trainer with id {}", user.getId());
        return trainerRepository.save(trainer);
    }

    @Override
    @Transactional
    public Trainer updateTrainer(AuthUserDto authUserDto, TrainerDto trainerDto){
        log.info("Updating trainer with username {}", authUserDto.getUsername());
        userService.userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword());
        User user1 = userService.updateUser(authUserDto.getUsername(), trainerDto);
        Trainer trainer = trainerRepository.findByUserId(user1.getId()).orElse(null);
        nullChecker(trainer, "Trainer");
        trainer.setSpecialization(trainerDto.getSpecialization() != null ? trainingTypeService.findByName(trainerDto.getSpecialization()) : trainer.getSpecialization());
        return trainerRepository.update(trainer);
    }

    @Override
    @Transactional(readOnly = true)
    public Trainer findByUsername(AuthUserDto authUserDto) {
        userService.userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword());
        User user = userService.findUserByUsername(authUserDto.getUsername());
        return trainerRepository.findByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("Trainer not found with userId" + user.getId()));
    }

    @Override
    @Transactional
    public Trainer passwordChange(ProfilePasswordChange profilePasswordChange) {
        userService.userAuthenticated(profilePasswordChange.username(), profilePasswordChange.oldPassword());
        User user = userService.findUserByUsername(profilePasswordChange.username());
        stringChecker(profilePasswordChange.newPassword(), "new password");
        userService.changePassword(user, profilePasswordChange.newPassword());
        return trainerRepository.findByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("Trainer not found with userId" + user.getId()));

    }

    @Override
    @Transactional
    public void changeStatusTrainer(TraineeStatusChangeDto traineeStatusChangeDto) {
        AuthUserDto authUserDto = traineeStatusChangeDto.authUser();
        userService.userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword());
        User user = userService.findUserByUsername(authUserDto.getUsername());
        userService.changeStatus(user, traineeStatusChangeDto.status());
        trainerRepository.findByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("Trainer not found with userId" + user.getId()));

    }


    @Transactional(readOnly = true)
    @Override
    public List<Training> getTrainingsByCriteria(AuthUserDto authUserDto, TrainerCriteriaDto trainerCriteriaDto) {
        userService.userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword());
        User user  = userService.findUserByUsername(authUserDto.getUsername());
       return trainingRepository.findByCriteria(user.getUsername(), trainerCriteriaDto);

    }

    @Transactional
    @Override
    public List<Trainer> updateTraineeTrainersList(AuthUserDto authUserDto, List<TrainerDto> trainerDtos) {
        userService.userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword());
        User user = userService.findUserByUsername(authUserDto.getUsername());
        Trainee trainee = traineeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("user doesn't have trainee profile"));
        nullChecker(trainee, "trainee");

        List<Long> newTrainerIds = trainerDtos.stream()
                .map(TrainerDto::getId)
                .toList();

        List<Trainer> newTrainers = new ArrayList<>();
        for(Long trainerId : newTrainerIds) {
            Trainer trainer = trainerRepository.findById(trainerId)
                    .orElseThrow(() -> new NoSuchElementException("Trainer not found with id " + trainerId));
            newTrainers.add(trainer);
        }

        List<Trainer> currentTrainers = trainee.getTrainers();

        List<Trainer> toRemove = currentTrainers.stream()
                .filter(t -> !newTrainers.contains(t))
                .toList();

        List<Trainer> toAdd = newTrainers.stream()
                .filter(t -> !currentTrainers.contains(t))
                .toList();

        for (Trainer trainer : toRemove) {
            trainer.getTrainees().remove(trainee);
            trainerRepository.save(trainer);
        }

        for (Trainer trainer : toAdd) {
            trainer.getTrainees().add(trainee);
            trainerRepository.save(trainer);
        }

        return newTrainers;
    }

}
