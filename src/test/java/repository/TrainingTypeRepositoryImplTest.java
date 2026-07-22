package repository;

import jakarta.persistence.NoResultException;
import kg.biamino.projects.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TrainingTypeRepositoryImplTest extends AbstractRepositoryTest {

    private TrainingTypeRepositoryImpl trainingTypeRepository;

    @BeforeEach
    void setUp() {
        trainingTypeRepository = injectEntityManager(new TrainingTypeRepositoryImpl(), "em");
    }

    @Test
    void save_newTrainingType_persistsAndGeneratesId() {
        TrainingType trainingType = new TrainingType("individual");

        TrainingType result = trainingTypeRepository.save(trainingType);
        flushAndClear();

        assertNotNull(result.getId());
        assertNotNull(entityManager.find(TrainingType.class, result.getId()));
    }

    @Test
    void save_existingTrainingType_merges() {
        TrainingType trainingType = persistTrainingType("individual");
        flushAndClear();
        TrainingType managed = entityManager.find(TrainingType.class, trainingType.getId());
        managed.setName("group");

        trainingTypeRepository.save(managed);
        flushAndClear();

        assertEquals("group", entityManager.find(TrainingType.class, trainingType.getId()).getName());
    }

    @Test
    void findById_found_returnsTrainingType() {
        TrainingType trainingType = persistTrainingType("individual");
        flushAndClear();

        Optional<TrainingType> result = trainingTypeRepository.findById(trainingType.getId());

        assertTrue(result.isPresent());
        assertEquals("individual", result.get().getName());
    }

    @Test
    void findById_notFound_returnsEmpty() {
        Optional<TrainingType> result = trainingTypeRepository.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByName_found_returnsTrainingType() {
        TrainingType trainingType = persistTrainingType("individual");
        flushAndClear();

        Optional<TrainingType> result = trainingTypeRepository.findByName("individual");

        assertTrue(result.isPresent());
        assertEquals(trainingType.getId(), result.get().getId());
    }

    @Test
    void findByName_notFound_throwsNoResultException() {
        assertThrows(NoResultException.class, () -> trainingTypeRepository.findByName("nonexistent"));
    }

    @Test
    void findAll_returnsAllPersistedTrainingTypes() {
        persistTrainingType("individual");
        persistTrainingType("group");
        flushAndClear();

        List<TrainingType> result = trainingTypeRepository.findAll();

        assertEquals(2, result.size());
    }
}
