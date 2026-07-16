package kg.biamino.projects.repository;

import kg.biamino.projects.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository {
    Trainee save(Trainee trainee);

    Trainee update(Trainee trainee);

    Optional<Trainee> findById(Long id) ;

    List<Trainee> findAll();

    void deleteById(Long id);

    Optional<Trainee> findByUsername(String user);

    Optional<Trainee> findByUserId(Long id);

    Optional<Trainee> findByUserIdToGetTrainers(Long id);
}
