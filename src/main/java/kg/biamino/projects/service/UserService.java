package kg.biamino.projects.service;

import kg.biamino.projects.dto.UserDto;
import kg.biamino.projects.model.User;

public interface UserService {
    User createUser(UserDto user);

    User updateUsersName(String username, UserDto userDto);
}
