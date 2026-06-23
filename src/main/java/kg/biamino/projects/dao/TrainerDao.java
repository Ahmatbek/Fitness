package kg.biamino.projects.dao;

import kg.biamino.projects.dto.TrainerDto;
import kg.biamino.projects.model.Trainer;

import java.util.List;

public interface TrainerDao {
    Trainer getTrainer(String id);

    Trainer createTrainer(String id, Trainer trainer);

    Trainer updateTrainer(String id, Trainer trainer);

    List<Trainer> getAllTrainers();
}
