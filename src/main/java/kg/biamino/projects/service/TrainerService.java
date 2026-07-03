package kg.biamino.projects.service;

import kg.biamino.projects.dto.AuthUserDto;
import kg.biamino.projects.dto.TrainerDto;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.records.ProfilePasswordChange;

import javax.naming.AuthenticationException;
import java.util.List;

public interface TrainerService {
    Trainer getTrainerById(Long id);

    List<Trainer> getAllTrainers();

    Trainer createTrainer(TrainerDto trainer);

    Trainer updateTrainer(String username, TrainerDto trainer);

    Trainer findByUsername(AuthUserDto authUserDto) throws AuthenticationException;

    Trainer passwordChange(ProfilePasswordChange profilePasswordChange) throws AuthenticationException;
}
