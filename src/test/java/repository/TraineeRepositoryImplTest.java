package repository;

import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.model.User;
import kg.biamino.projects.repository.TraineeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TraineeRepositoryImplTest extends AbstractRepositoryTest {

    private TraineeRepository traineeRepository;

    @BeforeEach
    void setUp() {
        traineeRepository = new TraineeRepositoryImpl();
    }

    @Test
    void save_persistsAndGeneratesId() {
        User user = persistUser("Dilmurod", "Sadyrov");
        Trainee trainee = new Trainee();
        trainee.setUser(user);

        Trainee result = traineeRepository.save(trainee);
        flushAndClear();

        assertNotNull(result.getId());
        assertNotNull(entityManager.find(Trainee.class, result.getId()));
    }

    @Test
    void update_mergesChanges() {
        User user = persistUser("Dilmurod", "Sadyrov");
        Trainee trainee = persistTrainee(user);
        flushAndClear();
        Trainee managed = entityManager.find(Trainee.class, trainee.getId());
        managed.setAddress("Bishkek");

        Trainee result = traineeRepository.update(managed);
        flushAndClear();

        assertEquals("Bishkek", entityManager.find(Trainee.class, result.getId()).getAddress());
    }

    @Test
    void findById_found_returnsTrainee() {
        User user = persistUser("Dilmurod", "Sadyrov");
        Trainee trainee = persistTrainee(user);
        flushAndClear();

        Optional<Trainee> result = traineeRepository.findById(trainee.getId());

        assertTrue(result.isPresent());
        assertEquals(trainee.getId(), result.get().getId());
    }

    @Test
    void findById_notFound_returnsEmpty() {
        Optional<Trainee> result = traineeRepository.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_returnsAllPersistedTrainees() {
        persistTrainee(persistUser("Dilmurod", "Sadyrov"));
        persistTrainee(persistUser("Aidana", "Toktosunova"));
        flushAndClear();

        List<Trainee> result = traineeRepository.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void deleteById_removesTrainee() {
        User user = persistUser("Dilmurod", "Sadyrov");
        Trainee trainee = persistTrainee(user);
        flushAndClear();

        traineeRepository.deleteById(trainee.getId());
        flushAndClear();

        assertNull(entityManager.find(Trainee.class, trainee.getId()));
    }

    @Test
    void findByUsername_found_returnsTrainee() {
        User user = persistUser("Dilmurod", "Sadyrov");
        Trainee trainee = persistTrainee(user);
        flushAndClear();

        Optional<Trainee> result = traineeRepository.findByUsername(user.getUsername());

        assertTrue(result.isPresent());
        assertEquals(trainee.getId(), result.get().getId());
    }

    @Test
    void findByUsername_notFound_returnsEmpty() {
        Optional<Trainee> result = traineeRepository.findByUsername("nonexistent.user");

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserId_found_returnsTrainee() {
        User user = persistUser("Dilmurod", "Sadyrov");
        Trainee trainee = persistTrainee(user);
        flushAndClear();

        Optional<Trainee> result = traineeRepository.findByUserId(user.getId());

        assertTrue(result.isPresent());
        assertEquals(trainee.getId(), result.get().getId());
    }

    @Test
    void findByUserId_notFound_returnsEmpty() {
        Optional<Trainee> result = traineeRepository.findByUserId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserIdToGetTrainers_traineeHasTrainer_returnsTraineeWithTrainersFetched() {
        User traineeUser = persistUser("Dilmurod", "Sadyrov");
        Trainee trainee = persistTrainee(traineeUser);
        TrainingType trainingType = persistTrainingType("individual");
        User trainerUser = persistUser("Aidana", "Toktosunova");
        Trainer trainer = persistTrainer(trainerUser, trainingType);
        trainer.setTrainees(new ArrayList<>(List.of(trainee)));
        flushAndClear();

        Optional<Trainee> result = traineeRepository.findByUserIdToGetTrainers(traineeUser.getId());

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getTrainers().size());
        assertEquals(trainer.getId(), result.get().getTrainers().get(0).getId());
    }

    @Test
    void findByUserIdToGetTrainers_traineeHasNoTrainer_returnsEmpty() {
        User traineeUser = persistUser("Dilmurod", "Sadyrov");
        persistTrainee(traineeUser);
        flushAndClear();

        Optional<Trainee> result = traineeRepository.findByUserIdToGetTrainers(traineeUser.getId());

        assertTrue(result.isEmpty());
    }
}
