package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Boots a private in-memory H2 database (via the "test" persistence unit) and wraps every
 * test in a transaction that is rolled back afterward, so repository tests never leak state
 * into one another.
 */
public abstract class AbstractRepositoryTest {

    private static EntityManagerFactory entityManagerFactory;
    private static final AtomicInteger USERNAME_SEQ = new AtomicInteger();

    protected EntityManager entityManager;

    @BeforeAll
    static void createEntityManagerFactory() {
        entityManagerFactory = Persistence.createEntityManagerFactory("test", Map.of(
                "jakarta.persistence.jdbc.url", "jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1"
        ));
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        entityManagerFactory.close();
    }

    @BeforeEach
    void openEntityManagerAndBeginTransaction() {
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
    }

    @AfterEach
    void rollbackTransactionAndCloseEntityManager() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
        }
        entityManager.close();
    }

    protected User persistUser(String firstName, String lastName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(firstName + "." + lastName + USERNAME_SEQ.incrementAndGet());
        user.setPassword("pass123");
        user.setIsActive(true);
        entityManager.persist(user);
        return user;
    }

    protected TrainingType persistTrainingType(String name) {
        TrainingType trainingType = new TrainingType(name);
        entityManager.persist(trainingType);
        return trainingType;
    }

    protected Trainee persistTrainee(User user) {
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        entityManager.persist(trainee);
        return trainee;
    }

    protected Trainer persistTrainer(User user, TrainingType specialization) {
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        entityManager.persist(trainer);
        return trainer;
    }

    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    /**
     * The repository impls rely on Spring to inject the {@code @PersistenceContext} field;
     * outside a container we set it directly onto the test's transactional EntityManager.
     */
    protected <T> T injectEntityManager(T repository, String fieldName) {
        try {
            Field field = repository.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(repository, entityManager);
            return repository;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
