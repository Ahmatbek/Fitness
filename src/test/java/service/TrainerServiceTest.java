package service;

import kg.biamino.projects.dto.AuthUserDto;
import kg.biamino.projects.dto.TraineeStatusChangeDto;
import kg.biamino.projects.dto.TrainerDto;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.model.User;
import kg.biamino.projects.records.ProfilePasswordChange;
import kg.biamino.projects.records.TrainerCriteriaDto;
import kg.biamino.projects.repository.TraineeRepository;
import kg.biamino.projects.repository.TrainerRepository;
import kg.biamino.projects.repository.TrainingRepository;
import kg.biamino.projects.service.TrainingTypeService;
import kg.biamino.projects.service.UserService;
import kg.biamino.projects.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TrainingTypeService trainingTypeService;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private UserService userService;

    private TrainerServiceImpl trainerService;

    private User user;
    private Trainer trainer;
    private Trainee trainee;
    private final AuthUserDto authUserDto = new AuthUserDto("Aidana.Toktosunova", "pass123");

    @BeforeEach
    void setUp() {
        trainerService = new TrainerServiceImpl(userService, trainingTypeService);
        trainerService.setTrainerDao(trainerRepository);
        trainerService.setTraineeRepository(traineeRepository);
        trainerService.setTrainingRepository(trainingRepository);

        user = new User();
        user.setId(2L);
        user.setUsername("Aidana.Toktosunova");
        user.setPassword("pass123");
        user.setIsActive(true);

        trainer = new Trainer();
        trainer.setId(20L);
        trainer.setUser(user);
        trainer.setSpecialization(new TrainingType("individual"));
        trainer.setTrainees(new ArrayList<>());

        trainee = new Trainee();
        trainee.setId(10L);
        trainee.setTrainers(new ArrayList<>());
    }

    @Test
    void getTrainerById_found_returnsTrainer() {
        when(trainerRepository.findById(20L)).thenReturn(Optional.of(trainer));

        assertEquals(trainer, trainerService.getTrainerById(20L));
    }

    @Test
    void getTrainerById_notFound_throwsNoSuchElementException() {
        when(trainerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainerService.getTrainerById(99L));
    }

    @Test
    void getAllTrainers_returnsRepositoryResult() {
        List<Trainer> trainers = List.of(trainer);
        when(trainerRepository.findAll()).thenReturn(trainers);

        assertEquals(trainers, trainerService.getAllTrainers());
    }

    @Test
    void createTrainer_success_setsSpecializationAndUser() {
        TrainerDto dto = new TrainerDto("Aidana", "Toktosunova", "individual");
        TrainingType type = new TrainingType("individual");
        when(userService.createUser(dto)).thenReturn(user);
        when(trainingTypeService.findByName("individual")).thenReturn(type);
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainer result = trainerService.createTrainer(dto);

        assertEquals(user, result.getUser());
        assertEquals(type, result.getSpecialization());
    }

    @Test
    void createTrainer_missingSpecialization_throwsIllegalArgumentException() {
        TrainerDto dto = new TrainerDto("Aidana", "Toktosunova", null);
        when(userService.createUser(dto)).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> trainerService.createTrainer(dto));
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void createTrainer_nullDto_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.createTrainer(null));
        verifyNoInteractions(userService);
    }

    @Test
    void updateTrainer_success_updatesSpecialization() {
        TrainerDto dto = new TrainerDto("Aidana", "Toktosunova", "group");
        TrainingType group = new TrainingType("group");
        when(userService.updateUser("Aidana.Toktosunova", dto)).thenReturn(user);
        when(trainerRepository.findByUserId(user.getId())).thenReturn(Optional.of(trainer));
        when(trainingTypeService.findByName("group")).thenReturn(group);
        when(trainerRepository.update(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainer result = trainerService.updateTrainer(authUserDto, dto);

        assertEquals(group, result.getSpecialization());
    }

    @Test
    void updateTrainer_trainerProfileMissing_throwsIllegalArgumentException() {
        TrainerDto dto = new TrainerDto("Aidana", "Toktosunova", "group");
        when(userService.updateUser("Aidana.Toktosunova", dto)).thenReturn(user);
        when(trainerRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> trainerService.updateTrainer(authUserDto, dto));
    }

    @Test
    void findByUsername_success_returnsTrainer() {
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(trainerRepository.findByUserId(user.getId())).thenReturn(Optional.of(trainer));

        assertEquals(trainer, trainerService.findByUsername(authUserDto));
    }

    @Test
    void findByUsername_notFound_throwsNoSuchElementException() {
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(trainerRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainerService.findByUsername(authUserDto));
    }

    @Test
    void passwordChange_success_delegatesToUserService() {
        ProfilePasswordChange change = new ProfilePasswordChange("Aidana.Toktosunova", "newPass", "pass123");
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(trainerRepository.findByUserId(user.getId())).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.passwordChange(change);

        assertEquals(trainer, result);
        verify(userService).changePassword(user, "newPass");
    }

    @Test
    void passwordChange_blankNewPassword_throwsIllegalArgumentException() {
        ProfilePasswordChange change = new ProfilePasswordChange("Aidana.Toktosunova", " ", "pass123");
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> trainerService.passwordChange(change));
        verify(userService, never()).changePassword(any(), any());
    }

    @Test
    void changeStatusTrainer_success_delegatesToUserService() {
        TraineeStatusChangeDto statusChangeDto = new TraineeStatusChangeDto(authUserDto, false);
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(trainerRepository.findByUserId(user.getId())).thenReturn(Optional.of(trainer));

        trainerService.changeStatusTrainer(statusChangeDto);

        verify(userService).changeStatus(user, false);
    }

    @Test
    void getTrainingsByCriteria_success_delegatesWithUsername() {
        TrainerCriteriaDto criteria = new TrainerCriteriaDto(2L, "Dilmurod",
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(2));
        List<Training> trainings = List.of(new Training());
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(trainingRepository.findByCriteria("Aidana.Toktosunova", criteria)).thenReturn(trainings);

        List<Training> result = trainerService.getTrainingsByCriteria(authUserDto, criteria);

        assertEquals(trainings, result);
    }

    @Test
    void updateTraineeTrainersList_addsNewTrainer() {
        Trainer newTrainer = new Trainer();
        newTrainer.setId(30L);
        newTrainer.setTrainees(new ArrayList<>());

        TrainerDto dto = new TrainerDto(30L, "Bekzat", "Isakov", "group");
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(traineeRepository.findByUserId(user.getId())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(30L)).thenReturn(Optional.of(newTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Trainer> result = trainerService.updateTraineeTrainersList(authUserDto, List.of(dto));

        assertEquals(List.of(newTrainer), result);
        assertTrue(newTrainer.getTrainees().contains(trainee));
    }

    @Test
    void updateTraineeTrainersList_removesTrainerNotInNewList() {
        trainee.setTrainers(new ArrayList<>(List.of(trainer)));
        trainer.getTrainees().add(trainee);

        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(traineeRepository.findByUserId(user.getId())).thenReturn(Optional.of(trainee));
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Trainer> result = trainerService.updateTraineeTrainersList(authUserDto, List.of());

        assertTrue(result.isEmpty());
        assertFalse(trainer.getTrainees().contains(trainee));
    }

    @Test
    void updateTraineeTrainersList_unknownTrainerId_throwsImmediatelyNoNPE() {
        TrainerDto dto = new TrainerDto(999L, "Kurmanbek", "Osmonov", "group");
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(traineeRepository.findByUserId(user.getId())).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> trainerService.updateTraineeTrainersList(authUserDto, List.of(dto)));
    }

    @Test
    void updateTraineeTrainersList_noTraineeProfile_throwsNoSuchElementException() {
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(traineeRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> trainerService.updateTraineeTrainersList(authUserDto, List.of()));
    }
}
