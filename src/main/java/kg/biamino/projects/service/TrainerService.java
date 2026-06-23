package kg.biamino.projects.service;

import kg.biamino.projects.dto.TrainerDto;
import kg.biamino.projects.model.Trainer;

import java.util.List;

public interface TrainerService {
    Trainer getTrainer(Long id);

    List<Trainer> getAllTrainers();

    Trainer createTrainer(TrainerDto trainer);

    Trainer updateTrainer(String username, TrainerDto trainer);
}
