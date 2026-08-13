package kg.biamino.projects.service;

import kg.biamino.projects.TrainerSummaryResponse;
import kg.biamino.projects.dto.*;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TrainerService {
    Trainer getTrainerById(Long id);
    Trainer getTrainerByUsername(String name);

    UserCredentialsDto createTrainer(TrainerDto trainer);

    TrainerTraineesListDto updateTrainer(UpdateTrainerDto trainer, String authUsername);

    @Transactional(readOnly = true)
    TrainerTraineesListDto findByUsername(String username);

    void changeStatusTrainer(ChangeStatusDto changeStatusDto, String authUsername);

    @Transactional(readOnly = true)
    List<TrainingsDisplayInfoTrainer> getTrainingsByCriteria(TrainerTrainingsDto trainerCriteriaDto);

    @Transactional
    List<Trainer> updateTraineeTrainersList(AuthUserDto authUserDto, List<TrainerDto> trainerDtos, Long id);

    ResponseEntity<TrainerSummaryResponse> getSummaryByUsername(String username);
}
