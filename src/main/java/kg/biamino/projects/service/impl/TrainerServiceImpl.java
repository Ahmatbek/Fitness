package kg.biamino.projects.service.impl;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import jakarta.persistence.criteria.Predicate;
import kg.biamino.projects.dto.*;
import kg.biamino.projects.exception.AuthorizationException;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.User;
import kg.biamino.projects.repository.*;
import kg.biamino.projects.service.TrainerService;
import kg.biamino.projects.service.TrainingTypeService;
import kg.biamino.projects.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static kg.biamino.projects.utils.ValidationInput.nullChecker;

@Service
@Slf4j
@Counted(value = "trainer.methods", description = "userService number of times each method is called")
@Timed(value = "trainer", description = "amount of time each method executes")
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
    @Timed(value = "trainer.create.time", description = "Time to create trainer")
    public Trainer getTrainerByUsername(String username) {
        log.info("Getting trainer with username {}", username);
        User user = userService.findUserByUsername(username);
        return trainerRepository.findByUserId(user.getId()).orElseThrow(()-> new NoSuchElementException("Trainer with username " + username + " not found"));
    }

    @Override
    @Transactional
    public UserCredentialsDto createTrainer(TrainerDto trainerDto) {
        nullChecker(trainerDto, "trainerDto");
        NewUserCredentials newUserCredentials = userService.createUser(trainerDto);

        Trainer trainer = new Trainer();
        trainer.setSpecialization(trainingTypeService.findByName(trainerDto.getSpecialization()));
        trainer.setUser(newUserCredentials.user());

        trainerRepository.save(trainer);
        log.info("Creating trainer with id {}", newUserCredentials.user().getId());


        return UserCredentialsDto.builder()
                .username(newUserCredentials.user().getUsername())
                .password(newUserCredentials.password())
                .build();
    }

    @Override
    @Transactional
    public TrainerTraineesListDto updateTrainer(UpdateTrainerDto trainerDto, String authUsername){
        log.info("Updating trainer with username {}", authUsername);
        if (!trainerDto.getUsername().equals(authUsername)) {
            throw new AuthorizationException("You dont have access to change others accounts");
        }

        User user1 = userService.updateUser(authUsername, trainerDto, trainerDto.getIsActive());
        Trainer trainer = trainerRepository.findByUserId(user1.getId()).orElseThrow(()-> new NoSuchElementException("Trainer with id " + user1.getId() + " not found"));

        log.info("Updating trainer with id {}", user1.getId());

        return TrainerTraineesListDto.builder()
                .isActive(user1.getIsActive())
                .firstName(user1.getFirstName())
                .lastName(user1.getLastName())
                .username(user1.getUsername())
                .specialization(trainer.getSpecialization().getName())
                .trainees(trainer.getTrainees()
                        .stream()
                        .map(this::toTraineeUsernameDto)
                        .toList())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public TrainerTraineesListDto findByUsername(String username) {
        User user = userService.findUserByUsername(username);
        Trainer trainer = trainerRepository.findByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("Trainer not found with userId" + user.getId()));

        return TrainerTraineesListDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .specialization(trainer.getSpecialization().getName())
                .isActive(user.getIsActive())
                .trainees(trainer.getTrainees().stream().map(this::toTraineeUsernameDto).toList())
                .build();
    }


    @Override
    @Transactional
    public void changeStatusTrainer(ChangeStatusDto changeStatusDto, String authUsername) {
        if (!changeStatusDto.getUsername().equals(authUsername)) {
            throw new AuthorizationException("You dont have access to change others accounts");
        }
        User user = userService.findUserByUsername(authUsername);
        trainerRepository.findByUserId(user.getId()).orElseThrow(() -> new NoSuchElementException("Trainer not found with userId" + user.getId()));
        userService.changeStatus(user, changeStatusDto.getIsActive());

    }


    @Transactional(readOnly = true)
    @Override
    public List<TrainingsDisplayInfoTrainer> getTrainingsByCriteria(TrainerTrainingsDto criteria) {
        Specification<Training> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(
                    root.join("trainer").join("user").get("username"),
                    criteria.getUsername()
            ));

            if (criteria.getFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), criteria.getFrom()));
            }

            if (criteria.getTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), criteria.getTo()));
            }

            if (criteria.getTraineeName() != null) {
                predicates.add(cb.equal(
                        root.join("trainee").join("user").get("firstName"),
                        criteria.getTraineeName()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

       List<Training> trainings = trainingRepository.findAll(spec);
       return trainings.stream().map(this::toTrainerTrainingsDto).toList();

    }

    @Transactional
    @Override
    public List<Trainer> updateTraineeTrainersList(AuthUserDto authUserDto, List<TrainerDto> trainerDtos, Long id) {
        userService.userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword());
        User user = userService.findUserByUsername(authUserDto.getUsername());
        Trainee trainee = traineeRepository.findTraineeByUserId(user.getId())
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

    private TraineeUsernameDto toTraineeUsernameDto(Trainee trainee) {
        return TraineeUsernameDto.builder()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .username(trainee.getUser().getUsername())
                .build();
    }

    private TrainingsDisplayInfoTrainer toTrainerTrainingsDto(Training training) {
        return TrainingsDisplayInfoTrainer.builder()
                .traineeName(training.getTrainee().getUser().getFirstName())
                .trainingDate(training.getDate())
                .trainingDuration(training.getDuration())
                .trainingName(training.getTrainingName())
                .trainingType(training.getTrainingType().getName())
                .build();
    }

}
