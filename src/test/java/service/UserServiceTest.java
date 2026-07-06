package service;

import kg.biamino.projects.dto.UserDto;
import kg.biamino.projects.model.User;
import kg.biamino.projects.repository.UserRepository;
import kg.biamino.projects.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
        userService.setUserDao(userRepository);
    }

    private User existingUser(String username, String password, boolean active) {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Nurlan");
        user.setLastName("Bekov");
        user.setUsername(username);
        user.setPassword(password);
        user.setIsActive(active);
        return user;
    }

    @Test
    void getAllUsers_returnsRepositoryResult() {
        List<User> users = List.of(existingUser("Nurlan.Bekov", "pass", true));
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(users, result);
    }

    @Test
    void createUser_success_generatesUsernameAndPassword() {
        UserDto dto = new UserDto("Nurlan", "Bekov");
        when(userRepository.findUserByUsername("Nurlan.Bekov")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.createUser(dto);

        assertNotNull(result);
        assertEquals("Nurlan", result.getFirstName());
        assertEquals("Bekov", result.getLastName());
        assertEquals("Nurlan.Bekov", result.getUsername());
        assertNotNull(result.getPassword());
        assertEquals(10, result.getPassword().length());
        assertTrue(result.getIsActive());
    }

    @Test
    void createUser_usernameCollision_appendsSequence() {
        UserDto dto = new UserDto("Nurlan", "Bekov");
        when(userRepository.findUserByUsername("Nurlan.Bekov")).thenReturn(Optional.of(existingUser("Nurlan.Bekov", "x", true)));
        when(userRepository.findUserByUsername("Nurlan.Bekov1")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.createUser(dto);

        assertEquals("Nurlan.Bekov1", result.getUsername());
    }

    @Test
    void createUser_usernameDoubleCollision_incrementsSequence() {
        UserDto dto = new UserDto("Nurlan", "Bekov");
        when(userRepository.findUserByUsername("Nurlan.Bekov")).thenReturn(Optional.of(existingUser("Nurlan.Bekov", "x", true)));
        when(userRepository.findUserByUsername("Nurlan.Bekov1")).thenReturn(Optional.of(existingUser("Nurlan.Bekov1", "x", true)));
        when(userRepository.findUserByUsername("Nurlan.Bekov2")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.createUser(dto);

        assertEquals("Nurlan.Bekov2", result.getUsername());
    }

    @Test
    void createUser_blankFirstName_throwsIllegalArgumentException() {
        UserDto dto = new UserDto("", "Bekov");
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
    }

    @Test
    void createUser_blankLastName_throwsIllegalArgumentException() {
        UserDto dto = new UserDto("Nurlan", "");
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
    }

    @Test
    void createUser_nullDto_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(null));
    }

    @Test
    void updateUser_success_updatesChangedFieldsOnly() {
        User user = existingUser("Nurlan.Bekov", "pass", false);
        when(userRepository.findUserByUsername("Nurlan.Bekov")).thenReturn(Optional.of(user));

        UserDto dto = new UserDto("Aigerim", null);
        User result = userService.updateUser("Nurlan.Bekov", dto);

        assertEquals("Aigerim", result.getFirstName());
        assertEquals("Bekov", result.getLastName());
        assertFalse(result.getIsActive(), "updateUser must not silently reactivate the profile");
        verify(userRepository).update(user);
    }

    @Test
    void updateUser_nullDto_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser("Nurlan.Bekov", null));
    }

    @Test
    void updateUser_blankUsername_throwsIllegalArgumentException() {
        UserDto dto = new UserDto("Aigerim", "Bekov");
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(" ", dto));
    }

    @Test
    void updateUser_userNotFound_throwsNoSuchElementException() {
        when(userRepository.findUserByUsername("ghost")).thenReturn(Optional.empty());
        UserDto dto = new UserDto("Aigerim", "Bekov");

        assertThrows(NoSuchElementException.class, () -> userService.updateUser("ghost", dto));
    }

    @Test
    void deleteUser_delegatesToRepository() {
        userService.deleteUser(5L);
        verify(userRepository).deleteById(5L);
    }

    @Test
    void findUserByUsername_found_returnsUser() {
        User user = existingUser("Nurlan.Bekov", "pass", true);
        when(userRepository.findUserByUsername("Nurlan.Bekov")).thenReturn(Optional.of(user));

        assertEquals(user, userService.findUserByUsername("Nurlan.Bekov"));
    }

    @Test
    void findUserByUsername_notFound_throwsNoSuchElementException() {
        when(userRepository.findUserByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.findUserByUsername("ghost"));
    }

    @Test
    void userAuthenticated_correctPassword_doesNotThrow() {
        User user = existingUser("Nurlan.Bekov", "secret", true);
        when(userRepository.findUserByUsername("Nurlan.Bekov")).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.userAuthenticated("Nurlan.Bekov", "secret"));
    }

    @Test
    void userAuthenticated_wrongPassword_throwsRuntimeException() {
        User user = existingUser("Nurlan.Bekov", "secret", true);
        when(userRepository.findUserByUsername("Nurlan.Bekov")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> userService.userAuthenticated("Nurlan.Bekov", "wrong"));
    }

    @Test
    void userAuthenticated_userNotFound_throwsRuntimeException() {
        when(userRepository.findUserByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.userAuthenticated("ghost", "whatever"));
    }

    @Test
    void changePassword_success_updatesPassword() {
        User user = existingUser("Nurlan.Bekov", "old", true);

        userService.changePassword(user, "newPass123");

        assertEquals("newPass123", user.getPassword());
        verify(userRepository).update(user);
    }

    @Test
    void changePassword_blankNewPassword_throwsIllegalArgumentException() {
        User user = existingUser("Nurlan.Bekov", "old", true);

        assertThrows(IllegalArgumentException.class, () -> userService.changePassword(user, "   "));
    }

    @Test
    void changePassword_nullNewPassword_throwsIllegalArgumentException() {
        User user = existingUser("Nurlan.Bekov", "old", true);

        assertThrows(IllegalArgumentException.class, () -> userService.changePassword(user, null));
    }

    @Test
    void changeStatus_toDifferentValue_updatesStatus() {
        User user = existingUser("Nurlan.Bekov", "pass", true);

        userService.changeStatus(user, false);

        assertFalse(user.getIsActive());
        verify(userRepository).update(user);
    }

    @Test
    void changeStatus_sameValueAsCurrent_throwsIllegalStateException() {
        User user = existingUser("Nurlan.Bekov", "pass", true);

        assertThrows(IllegalStateException.class, () -> userService.changeStatus(user, true));
        verify(userRepository, never()).update(any());
    }

    @Test
    void changeStatus_nullStatus_throwsIllegalArgumentException() {
        User user = existingUser("Nurlan.Bekov", "pass", true);

        assertThrows(IllegalArgumentException.class, () -> userService.changeStatus(user, null));
    }
}
