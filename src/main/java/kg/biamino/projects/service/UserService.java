package kg.biamino.projects.service;

import kg.biamino.projects.dto.ChangePasswordDto;
import kg.biamino.projects.dto.NewUserCredentials;
import kg.biamino.projects.dto.UserDto;
import kg.biamino.projects.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    NewUserCredentials createUser(UserDto user);

    @Transactional
    User updateUser(String username, UserDto userDto, boolean status);

    void deleteUser(Long id);

    User findUserByUsername(String username);

    void userAuthenticated(String username, String password);

    void changePassword(ChangePasswordDto changePasswordDto, String authUsername);

    @Transactional
    void changeStatus(User user, Boolean status);
}
