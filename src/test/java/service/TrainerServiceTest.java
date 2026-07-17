package service;

import kg.biamino.projects.dto.AuthUserDto;
import kg.biamino.projects.dto.ChangeStatusDto;
import kg.biamino.projects.dto.TrainerDto;
import kg.biamino.projects.dto.TrainerTraineesListDto;
import kg.biamino.projects.dto.TrainerTrainingsDto;
import kg.biamino.projects.dto.TrainingsDisplayInfoTrainer;
import kg.biamino.projects.dto.UpdateTrainerDto;
import kg.biamino.projects.dto.UserCredentialsDto;
import kg.biamino.projects.exception.AuthorizationException;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.model.User;
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

    @BeforeEach
    void setUp() {
        trainerService = new TrainerServiceImpl(userService, trainingTypeService);
        trainerService.setTrainerDao(trainerRepository);
        trainerService.setTraineeRepository(traineeRepository);
        trainerService.setTrainingRepository(trainingRepository);

        user = new User();
        user.setId(2L);
        user.setUsername("Aidana.Toktosunova");
        user.setFirstName("Aidana");
        user.setLastName("Toktosunova");
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
    void getTrainerByUsername_found_returnsTrainer() {
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(trainerRepository.findByUserId(2L)).thenReturn(Optional.of(trainer));

        assertEquals(trainer, trainerService.getTrainerByUsername("Aidana.Toktosunova"));
    }

    @Test
    void getTrainerByUsername_notFound_throwsNoSuchElementException() {
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(trainerRepository.findByUserId(2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainerService.getTrainerByUsername("Aidana.Toktosunova"));
    }

    @Test
    void createTrainer_success_returnsCredentials() {
        TrainerDto dto = new TrainerDto();
        dto.setFirstName("Aidana");
        dto.setLastName("Toktosunova");
        dto.setSpecialization("individual");
        TrainingType type = new TrainingType("individual");
        when(userService.createUser(dto)).thenReturn(user);
        when(trainingTypeService.findByName("individual")).thenReturn(type);
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        UserCredentialsDto result = trainerService.createTrainer(dto);

        assertEquals("Aidana.Toktosunova", result.getUsername());
        assertEquals("pass123", result.getPassword());
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void createTrainer_nullDto_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.createTrainer(null));
        verifyNoInteractions(userService, trainerRepository);
    }

    @Test
    void updateTrainer_success_updatesActiveAndNames() {
        UpdateTrainerDto dto = new UpdateTrainerDto();
        dto.setUsername("Aidana.Toktosunova");
        dto.setFirstName("Aidana");
        dto.setLastName("Toktosunova");
        dto.setIsActive(true);
        when(userService.updateUser("Aidana.Toktosunova", dto, true)).thenReturn(user);
        when(trainerRepository.findByUserId(2L)).thenReturn(Optional.of(trainer));
        when(trainerRepository.update(trainer)).thenReturn(trainer);

        TrainerTraineesListDto result = trainerService.updateTrainer(dto, "Aidana.Toktosunova");

        assertEquals("Aidana", result.getFirstName());
        assertTrue(result.getIsActive());
        assertTrue(result.getTrainees().isEmpty());
    }

    @Test
    void updateTrainer_specializationIsReadOnly_ignoresRequestedValue() {
        UpdateTrainerDto dto = new UpdateTrainerDto();
        dto.setUsername("Aidana.Toktosunova");
        dto.setFirstName("Aidana");
        dto.setLastName("Toktosunova");
        dto.setSpecialization("group");
        dto.setIsActive(true);
        when(userService.updateUser("Aidana.Toktosunova", dto, true)).thenReturn(user);
        when(trainerRepository.findByUserId(2L)).thenReturn(Optional.of(trainer));
        when(trainerRepository.update(trainer)).thenReturn(trainer);

        TrainerTraineesListDto result = trainerService.updateTrainer(dto, "Aidana.Toktosunova");

        assertEquals("individual", result.getSpecialization());
        verifyNoInteractions(trainingTypeService);
    }

    @Test
    void updateTrainer_authorizationMismatch_throwsAuthorizationException() {
        UpdateTrainerDto dto = new UpdateTrainerDto();
        dto.setUsername("Someone.Else");
        dto.setIsActive(true);

        assertThrows(AuthorizationException.class, () -> trainerService.updateTrainer(dto, "Aidana.Toktosunova"));
        verifyNoInteractions(userService);
    }

    @Test
    void updateTrainer_trainerProfileMissing_throwsNoSuchElementException() {
        UpdateTrainerDto dto = new UpdateTrainerDto();
        dto.setUsername("Aidana.Toktosunova");
        dto.setIsActive(true);
        when(userService.updateUser("Aidana.Toktosunova", dto, true)).thenReturn(user);
        when(trainerRepository.findByUserId(2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainerService.updateTrainer(dto, "Aidana.Toktosunova"));
    }

    @Test
    void findByUsername_success_returnsTrainer() {
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(trainerRepository.findByUserId(2L)).thenReturn(Optional.of(trainer));

        TrainerTraineesListDto result = trainerService.findByUsername("Aidana.Toktosunova");

        assertEquals("individual", result.getSpecialization());
        assertEquals("Aidana", result.getFirstName());
        assertTrue(result.getIsActive());
    }

    @Test
    void findByUsername_notFound_throwsNoSuchElementException() {
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(trainerRepository.findByUserId(2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainerService.findByUsername("Aidana.Toktosunova"));
    }

    @Test
    void changeStatusTrainer_success_delegatesToUserService() {
        ChangeStatusDto dto = new ChangeStatusDto();
        dto.setUsername("Aidana.Toktosunova");
        dto.setIsActive(false);
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(trainerRepository.findByUserId(2L)).thenReturn(Optional.of(trainer));

        trainerService.changeStatusTrainer(dto, "Aidana.Toktosunova");

        verify(userService).changeStatus(user, false);
    }

    @Test
    void changeStatusTrainer_trainerProfileMissing_throwsNoSuchElementException() {
        ChangeStatusDto dto = new ChangeStatusDto();
        dto.setUsername("Aidana.Toktosunova");
        dto.setIsActive(false);
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(trainerRepository.findByUserId(2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainerService.changeStatusTrainer(dto, "Aidana.Toktosunova"));
        verify(userService, never()).changeStatus(any(), any());
    }

    @Test
    void getTrainingsByCriteria_success_mapsTrainings() {
        TrainerTrainingsDto criteria = new TrainerTrainingsDto("Aidana.Toktosunova",
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(2), null);

        Trainee traineeWithUser = new Trainee();
        User traineeUser = new User();
        traineeUser.setFirstName("Dilmurod");
        traineeWithUser.setUser(traineeUser);

        Training training = new Training();
        training.setTrainee(traineeWithUser);
        training.setTrainingType(new TrainingType("individual"));
        training.setTrainingName("Yoga");
        training.setDate(LocalDate.now());
        training.setDuration(60);

        when(trainingRepository.findByCriteria("Aidana.Toktosunova", criteria)).thenReturn(List.of(training));

        List<TrainingsDisplayInfoTrainer> result = trainerService.getTrainingsByCriteria(criteria);

        assertEquals(1, result.size());
        assertEquals("Dilmurod", result.get(0).getTraineeName());
        assertEquals("Yoga", result.get(0).getTrainingName());
        assertEquals("individual", result.get(0).getTrainingType());
        assertEquals(60, result.get(0).getTrainingDuration());
    }

    @Test
    void updateTraineeTrainersList_addsNewTrainer() {
        Trainer newTrainer = new Trainer();
        newTrainer.setId(30L);
        newTrainer.setTrainees(new ArrayList<>());

        TrainerDto dto = new TrainerDto();
        dto.setId(30L);
        AuthUserDto authUserDto = new AuthUserDto("Aidana.Toktosunova", "pass123");
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(traineeRepository.findByUserId(2L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(30L)).thenReturn(Optional.of(newTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Trainer> result = trainerService.updateTraineeTrainersList(authUserDto, List.of(dto), 10L);

        assertEquals(List.of(newTrainer), result);
        assertTrue(newTrainer.getTrainees().contains(trainee));
        verify(userService).userAuthenticated("Aidana.Toktosunova", "pass123");
    }

    @Test
    void updateTraineeTrainersList_removesTrainerNotInNewList() {
        trainee.setTrainers(new ArrayList<>(List.of(trainer)));
        trainer.getTrainees().add(trainee);

        AuthUserDto authUserDto = new AuthUserDto("Aidana.Toktosunova", "pass123");
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(traineeRepository.findByUserId(2L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Trainer> result = trainerService.updateTraineeTrainersList(authUserDto, List.of(), 10L);

        assertTrue(result.isEmpty());
        assertFalse(trainer.getTrainees().contains(trainee));
    }

    @Test
    void updateTraineeTrainersList_unknownTrainerId_throwsNoSuchElementException() {
        TrainerDto dto = new TrainerDto();
        dto.setId(999L);
        AuthUserDto authUserDto = new AuthUserDto("Aidana.Toktosunova", "pass123");
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(traineeRepository.findByUserId(2L)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> trainerService.updateTraineeTrainersList(authUserDto, List.of(dto), 10L));
    }

    @Test
    void updateTraineeTrainersList_noTraineeProfile_throwsNoSuchElementException() {
        AuthUserDto authUserDto = new AuthUserDto("Aidana.Toktosunova", "pass123");
        when(userService.findUserByUsername("Aidana.Toktosunova")).thenReturn(user);
        when(traineeRepository.findByUserId(2L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> trainerService.updateTraineeTrainersList(authUserDto, List.of(), 10L));
    }
}
