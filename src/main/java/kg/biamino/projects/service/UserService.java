package kg.biamino.projects.service;

import kg.biamino.projects.dto.UserDto;
import kg.biamino.projects.model.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User createUser(UserDto user);

    User updateUsersName(String username, UserDto userDto);

    void deleteUser(String username);

    User findUserByUsername(String username);
}
