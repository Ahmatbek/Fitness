package kg.biamino.projects.service;

import kg.biamino.projects.dto.AuthUserDto;
import kg.biamino.projects.dto.ProfileStatusChangeDto;
import kg.biamino.projects.dto.TrainerDto;
import kg.biamino.projects.dto.UserCredentialsDto;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.records.ProfilePasswordChange;
import kg.biamino.projects.records.TrainerCriteriaDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TrainerService {
    Trainer getTrainerById(Long id);

    List<Trainer> getAllTrainers();

    UserCredentialsDto createTrainer(TrainerDto trainer);

    Trainer updateTrainer(AuthUserDto authUserDto, TrainerDto trainer);

    Trainer findByUsername(AuthUserDto authUserDto);

    Trainer passwordChange(ProfilePasswordChange profilePasswordChange);

    void changeStatusTrainer(ProfileStatusChangeDto traineeStatusChangeDto);

    @Transactional(readOnly = true)
    List<Training> getTrainingsByCriteria(AuthUserDto authUserDto, TrainerCriteriaDto trainerCriteriaDto);

    @Transactional
    List<Trainer> updateTraineeTrainersList(AuthUserDto authUserDto, List<TrainerDto> trainerDtos, Long id);
}
