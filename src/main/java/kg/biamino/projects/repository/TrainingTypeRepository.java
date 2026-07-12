package kg.biamino.projects.repository;

import kg.biamino.projects.model.TrainingType;

import java.util.Optional;

public interface TrainingTypeRepository {
    TrainingType save(TrainingType trainingType);

    Optional<TrainingType> findById(Long id);

    Optional<TrainingType> findByName(String name);
}
