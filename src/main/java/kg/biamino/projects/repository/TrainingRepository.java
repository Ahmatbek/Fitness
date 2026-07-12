package kg.biamino.projects.repository;

import kg.biamino.projects.model.Training;
import kg.biamino.projects.records.TraineeCriteriaDto;
import kg.biamino.projects.records.TrainerCriteriaDto;

import java.util.List;
import java.util.Optional;

public interface TrainingRepository {
    Training save(Training training);

    Optional<Training> findById(Long id);

    List<Training> findAll();

    List<Training> findByCriteria(String  username, TraineeCriteriaDto traineeCriteriaDto);

    List<Training> findByCriteria(String username, TrainerCriteriaDto traineeCriteriaDto);
}
