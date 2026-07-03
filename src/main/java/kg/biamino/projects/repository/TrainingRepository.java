package kg.biamino.projects.repository;

import kg.biamino.projects.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository {
    Training save(Training training);

    Optional<Training> findById(Long id);

    List<Training> findAll();
}
