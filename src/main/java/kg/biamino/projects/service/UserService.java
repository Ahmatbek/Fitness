package kg.biamino.projects.service;

import kg.biamino.projects.dto.UserDto;
import kg.biamino.projects.model.User;
import kg.biamino.projects.records.ProfilePasswordChange;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User createUser(UserDto user);

    @Transactional
    User updateUser(Long id, UserDto userDto);

    void deleteUser(Long id);

    User findUserByUsername(String username);

    boolean userAuthenticated(String username, String password);

    void changePassword(User user, String newPassword);
}
