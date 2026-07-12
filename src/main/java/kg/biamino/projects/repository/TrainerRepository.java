package kg.biamino.projects.repository;

import kg.biamino.projects.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository {
    Trainer save(Trainer trainer);

    Optional<Trainer> findByUserId(Long id);

    Optional<Trainer> findById(Long id);

    List<Trainer> findAll();

    Trainer update(Trainer trainer);

    List<Trainer> findNotAssignedTrainees(Long traineeId);
}
