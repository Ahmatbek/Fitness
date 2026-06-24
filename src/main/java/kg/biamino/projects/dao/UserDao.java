package kg.biamino.projects.dao;

import kg.biamino.projects.model.User;

import java.util.List;

public interface UserDao {
    User getUserByUsername(String username);

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(String username);
}
