package repository;

import kg.biamino.projects.model.User;
import kg.biamino.projects.repository.impl.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryImplTest extends AbstractRepositoryTest {

    private UserRepositoryImpl userRepository;

    @BeforeEach
    void setUp() {
        userRepository = injectEntityManager(new UserRepositoryImpl(), "entityManager");
    }

    private User newTransientUser(String username) {
        User user = new User();
        user.setFirstName("Aidana");
        user.setLastName("Toktosunova");
        user.setUsername(username);
        user.setPassword("pass123");
        user.setIsActive(true);
        return user;
    }

    @Test
    void save_newUser_persistsAndGeneratesId() {
        User user = newTransientUser("Aidana.Toktosunova");

        User result = userRepository.save(user);
        flushAndClear();

        assertNotNull(result.getId());
        assertTrue(entityManager.find(User.class, result.getId()) != null);
    }

    @Test
    void save_existingUser_mergesChanges() {
        User user = persistUser("Aidana", "Toktosunova");
        flushAndClear();
        User managed = entityManager.find(User.class, user.getId());
        managed.setFirstName("Changed");

        User result = userRepository.save(managed);
        flushAndClear();

        assertEquals("Changed", entityManager.find(User.class, result.getId()).getFirstName());
    }

    @Test
    void findById_found_returnsUser() {
        User user = persistUser("Aidana", "Toktosunova");
        flushAndClear();

        Optional<User> result = userRepository.findById(user.getId());

        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
    }

    @Test
    void findById_notFound_returnsEmpty() {
        Optional<User> result = userRepository.findById(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_returnsAllPersistedUsers() {
        persistUser("Aidana", "Toktosunova");
        persistUser("Dilmurod", "Sadyrov");
        flushAndClear();

        List<User> result = userRepository.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void deleteById_removesUser() {
        User user = persistUser("Aidana", "Toktosunova");
        flushAndClear();

        userRepository.deleteById(user.getId());
        flushAndClear();

        assertNull(entityManager.find(User.class, user.getId()));
    }

    @Test
    void findUserByUsername_found_returnsUser() {
        User user = persistUser("Aidana", "Toktosunova");
        flushAndClear();

        Optional<User> result = userRepository.findUserByUsername(user.getUsername());

        assertTrue(result.isPresent());
        assertEquals(user.getUsername(), result.get().getUsername());
    }

    @Test
    void findUserByUsername_notFound_returnsEmpty() {
        Optional<User> result = userRepository.findUserByUsername("nonexistent.user");

        assertTrue(result.isEmpty());
    }

    @Test
    void update_mergesUserChanges() {
        User user = persistUser("Aidana", "Toktosunova");
        flushAndClear();
        User managed = entityManager.find(User.class, user.getId());
        managed.setIsActive(false);

        User result = userRepository.update(managed);
        flushAndClear();

        assertFalse(entityManager.find(User.class, result.getId()).getIsActive());
    }
}
