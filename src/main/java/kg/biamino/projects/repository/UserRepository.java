package kg.biamino.projects.repository;

import kg.biamino.projects.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    void deleteById(Long id);

    Optional<User> findUserByUsername(String username);

    User update(User user);
}
