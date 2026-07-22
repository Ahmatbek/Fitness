package repository;

import kg.biamino.projects.dto.TraineeTrainingsDto;
import kg.biamino.projects.dto.TrainerTrainingsDto;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TrainingRepositoryImplTest extends AbstractRepositoryTest {

    private TrainingRepositoryImpl trainingRepository;
    private TrainingType individual;
    private Trainee trainee;
    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainingRepository = injectEntityManager(new TrainingRepositoryImpl(), "entityManager");
        individual = persistTrainingType("individual");
        trainee = persistTrainee(persistUser("Dilmurod", "Sadyrov"));
        trainer = persistTrainer(persistUser("Aidana", "Toktosunova"), individual);
    }

    private Training newTraining(LocalDate date, String name) {
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(name);
        training.setTrainingType(individual);
        training.setDate(date);
        training.setDuration(60);
        return training;
    }

    @Test
    void save_newTraining_persistsAndGeneratesId() {
        Training training = newTraining(LocalDate.now(), "Yoga");

        Training result = trainingRepository.save(training);
        flushAndClear();

        assertNotNull(result.getId());
        assertNotNull(entityManager.find(Training.class, result.getId()));
    }

    @Test
    void save_existingTraining_merges() {
        Training training = newTraining(LocalDate.now(), "Yoga");
        entityManager.persist(training);
        flushAndClear();
        Training managed = entityManager.find(Training.class, training.getId());
        managed.setTrainingName("Pilates");

        Training result = trainingRepository.save(managed);
        flushAndClear();

        assertEquals("Pilates", entityManager.find(Training.class, result.getId()).getTrainingName());
    }

    @Test
    void findById_found_returnsTraining() {
        Training training = newTraining(LocalDate.now(), "Yoga");
        entityManager.persist(training);
        flushAndClear();

        Optional<Training> result = trainingRepository.findById(training.getId());

        assertTrue(result.isPresent());
        assertEquals("Yoga", result.get().getTrainingName());
    }

    @Test
    void findById_notFound_returnsEmpty() {
        Optional<Training> result = trainingRepository.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_returnsAllPersistedTrainings() {
        entityManager.persist(newTraining(LocalDate.now(), "Yoga"));
        entityManager.persist(newTraining(LocalDate.now(), "Pilates"));
        flushAndClear();

        List<Training> result = trainingRepository.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findByCriteria_traineeDto_filtersByUsernameOnly() {
        entityManager.persist(newTraining(LocalDate.now(), "Yoga"));
        flushAndClear();
        TraineeTrainingsDto criteria = TraineeTrainingsDto.builder().username(trainee.getUser().getUsername()).build();

        List<Training> result = trainingRepository.findByCriteria(trainee.getUser().getUsername(), criteria);

        assertEquals(1, result.size());
    }

    @Test
    void findByCriteria_traineeDto_filtersByDateRangeExcludesOutOfRange() {
        entityManager.persist(newTraining(LocalDate.now().minusDays(10), "Yoga"));
        flushAndClear();
        TraineeTrainingsDto criteria = TraineeTrainingsDto.builder()
                .username(trainee.getUser().getUsername())
                .from(LocalDate.now().minusDays(2))
                .to(LocalDate.now().plusDays(2))
                .build();

        List<Training> result = trainingRepository.findByCriteria(trainee.getUser().getUsername(), criteria);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByCriteria_traineeDto_filtersByTrainerNameAndTrainingType() {
        entityManager.persist(newTraining(LocalDate.now(), "Yoga"));
        flushAndClear();
        TraineeTrainingsDto matching = TraineeTrainingsDto.builder()
                .username(trainee.getUser().getUsername())
                .trainerName(trainer.getUser().getFirstName())
                .trainingType(individual.getName())
                .build();
        TraineeTrainingsDto nonMatching = TraineeTrainingsDto.builder()
                .username(trainee.getUser().getUsername())
                .trainerName("SomeoneElse")
                .build();

        assertEquals(1, trainingRepository.findByCriteria(trainee.getUser().getUsername(), matching).size());
        assertTrue(trainingRepository.findByCriteria(trainee.getUser().getUsername(), nonMatching).isEmpty());
    }

    @Test
    void findByCriteria_trainerDto_filtersByUsernameOnly() {
        entityManager.persist(newTraining(LocalDate.now(), "Yoga"));
        flushAndClear();
        TrainerTrainingsDto criteria = TrainerTrainingsDto.builder().username(trainer.getUser().getUsername()).build();

        List<Training> result = trainingRepository.findByCriteria(trainer.getUser().getUsername(), criteria);

        assertEquals(1, result.size());
    }

    @Test
    void findByCriteria_trainerDto_filtersByDateRangeAndTraineeName() {
        entityManager.persist(newTraining(LocalDate.now(), "Yoga"));
        flushAndClear();
        TrainerTrainingsDto matching = TrainerTrainingsDto.builder()
                .username(trainer.getUser().getUsername())
                .from(LocalDate.now().minusDays(1))
                .to(LocalDate.now().plusDays(1))
                .traineeName(trainee.getUser().getFirstName())
                .build();
        TrainerTrainingsDto outOfRange = TrainerTrainingsDto.builder()
                .username(trainer.getUser().getUsername())
                .from(LocalDate.now().plusDays(5))
                .to(LocalDate.now().plusDays(10))
                .build();

        assertEquals(1, trainingRepository.findByCriteria(trainer.getUser().getUsername(), matching).size());
        assertTrue(trainingRepository.findByCriteria(trainer.getUser().getUsername(), outOfRange).isEmpty());
    }
}
