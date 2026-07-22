package repository;

import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TrainerRepositoryImplTest extends AbstractRepositoryTest {

    private TrainerRepositoryImpl trainerRepository;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainerRepository = injectEntityManager(new TrainerRepositoryImpl(), "entityManager");
        trainingType = persistTrainingType("individual");
    }

    @Test
    void save_persistsAndGeneratesId() {
        User user = persistUser("Aidana", "Toktosunova");
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);

        Trainer result = trainerRepository.save(trainer);
        flushAndClear();

        assertNotNull(result.getId());
        assertNotNull(entityManager.find(Trainer.class, result.getId()));
    }

    @Test
    void findByUserId_found_returnsTrainer() {
        User user = persistUser("Aidana", "Toktosunova");
        Trainer trainer = persistTrainer(user, trainingType);
        flushAndClear();

        Optional<Trainer> result = trainerRepository.findByUserId(user.getId());

        assertTrue(result.isPresent());
        assertEquals(trainer.getId(), result.get().getId());
    }

    @Test
    void findByUserId_notFound_returnsEmpty() {
        Optional<Trainer> result = trainerRepository.findByUserId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_found_returnsTrainer() {
        User user = persistUser("Aidana", "Toktosunova");
        Trainer trainer = persistTrainer(user, trainingType);
        flushAndClear();

        Optional<Trainer> result = trainerRepository.findById(trainer.getId());

        assertTrue(result.isPresent());
        assertEquals(trainer.getId(), result.get().getId());
    }

    @Test
    void findById_notFound_returnsEmpty() {
        Optional<Trainer> result = trainerRepository.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_returnsAllPersistedTrainers() {
        persistTrainer(persistUser("Aidana", "Toktosunova"), trainingType);
        persistTrainer(persistUser("Nurlan", "Bekov"), trainingType);
        flushAndClear();

        List<Trainer> result = trainerRepository.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void update_mergesChanges() {
        User user = persistUser("Aidana", "Toktosunova");
        Trainer trainer = persistTrainer(user, trainingType);
        flushAndClear();
        Trainer managed = entityManager.find(Trainer.class, trainer.getId());
        managed.getUser().setFirstName("Changed");

        Trainer result = trainerRepository.update(managed);
        flushAndClear();

        assertEquals("Changed", entityManager.find(Trainer.class, result.getId()).getUser().getFirstName());
    }

    @Test
    void findNotAssignedTrainees_queryIsMalformed_throwsIllegalArgumentException() {
        // Characterizes a pre-existing bug: the JPQL's "join Trainer trainer" has no ON
        // clause, which recent Hibernate versions reject outright (see
        // TrainerRepositoryImpl#findNotAssignedTrainees).
        User traineeUser = persistUser("Dilmurod", "Sadyrov");
        Trainee trainee = persistTrainee(traineeUser);
        flushAndClear();

        assertThrows(IllegalArgumentException.class,
                () -> trainerRepository.findNotAssignedTrainees(trainee.getId()));
    }

    @Test
    void findNotAssignedTrainersByTraineeId_returnsTrainersAssignedToOtherTrainees() {
        User traineeUser = persistUser("Dilmurod", "Sadyrov");
        Trainee targetTrainee = persistTrainee(traineeUser);
        User otherTraineeUser = persistUser("Nazira", "Osmonova");
        Trainee otherTrainee = persistTrainee(otherTraineeUser);

        Trainer assignedToOther = persistTrainer(persistUser("Aidana", "Toktosunova"), trainingType);
        assignedToOther.setTrainees(new ArrayList<>(List.of(otherTrainee)));

        Trainer assignedOnlyToTarget = persistTrainer(persistUser("Nurlan", "Bekov"), trainingType);
        assignedOnlyToTarget.setTrainees(new ArrayList<>(List.of(targetTrainee)));

        Trainer unassigned = persistTrainer(persistUser("Baktygul", "Isakova"), trainingType);
        flushAndClear();

        List<Trainer> result = trainerRepository.findNotAssignedTrainersByTraineeId(targetTrainee.getId());

        List<Long> ids = result.stream().map(Trainer::getId).toList();
        assertTrue(ids.contains(assignedToOther.getId()));
        assertFalse(ids.contains(assignedOnlyToTarget.getId()));
        assertFalse(ids.contains(unassigned.getId()));
    }
}
