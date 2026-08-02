package service;

import jakarta.persistence.criteria.Predicate;
import kg.biamino.projects.dto.*;
import kg.biamino.projects.exception.AuthorizationException;
import kg.biamino.projects.exception.DateInvalidException;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.model.User;
import kg.biamino.projects.repository.TraineeRepository;
import kg.biamino.projects.repository.TrainerRepository;
import kg.biamino.projects.repository.TrainingRepository;
import kg.biamino.projects.service.UserService;
import kg.biamino.projects.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private UserService userService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private TraineeServiceImpl traineeService;

    private User user;
    private Trainee trainee;
    private NewUserCredentials credentials;

    @BeforeEach
    void setUp() {
        traineeService = new TraineeServiceImpl(userService);
        traineeService.setTrainerRepository(trainerRepository);
        traineeService.setTrainingRepository(trainingRepository);
        traineeService.setTraineeRepository(traineeRepository);

        user = new User();
        user.setId(1L);
        user.setUsername("Nurlan.Bekov");
        user.setFirstName("Nurlan");
        user.setLastName("Bekov");
        user.setPassword(passwordEncoder.encode("password"));
        user.setIsActive(true);

        trainee = new Trainee();
        trainee.setId(10L);
        trainee.setUser(user);
        trainee.setAddress("Bishkek");
        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        trainee.setTrainers(new ArrayList<>());

        credentials=new NewUserCredentials(user,"password");

    }

    @Test
    void getTraineeByUsername_found_returnsTrainee() {
        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
        when(traineeRepository.findTraineeByUserId(1L)).thenReturn(Optional.of(trainee));

        assertEquals(trainee, traineeService.getTraineeByUsername("Nurlan.Bekov"));
    }

    @Test
    void getTraineeByUsername_notFound_throwsNoSuchElementException() {
        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
        when(traineeRepository.findTraineeByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> traineeService.getTraineeByUsername("Nurlan.Bekov"));
    }

    @Test
    void createTrainee_success_returnsCredentials() {
        TraineeDto dto = new TraineeDto();
        dto.setFirstName("Nurlan");
        dto.setLastName("Bekov");
        dto.setAddress("Bishkek");
        dto.setDateOfBirth(LocalDate.of(2000, 1, 1));
        when(userService.createUser(dto)).thenReturn(credentials);
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        UserCredentialsDto result = traineeService.createTrainee(dto);

        assertEquals("Nurlan.Bekov", result.getUsername());
        assertEquals("password", result.getPassword());
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void createTrainee_nullDto_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.createTrainee(null));
        verifyNoInteractions(userService, traineeRepository);
    }

    @Test
    void createTrainee_futureDateOfBirth_throwsDateInvalidException() {
        TraineeDto dto = new TraineeDto();
        dto.setFirstName("Nurlan");
        dto.setLastName("Bekov");
        dto.setDateOfBirth(LocalDate.now().plusDays(1));

        assertThrows(DateInvalidException.class, () -> traineeService.createTrainee(dto));
        verifyNoInteractions(userService, traineeRepository);
    }

    @Test
    void updateTrainee_success_updatesAddressAndDateOfBirth() {
        UpdateTraineeDto dto = new UpdateTraineeDto();
        dto.setUsername("Nurlan.Bekov");
        dto.setAddress("Osh");
        dto.setDateOfBirth(LocalDate.of(1999, 5, 5));
        dto.setIsActive(true);
        when(userService.updateUser("Nurlan.Bekov", dto, true)).thenReturn(user);
        when(traineeRepository.findTraineeByUserId(1L)).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        TraineeTrainersListDto result = traineeService.updateTrainee(dto, "Nurlan.Bekov");

        assertEquals("Osh", result.getAddress());
        assertEquals(LocalDate.of(1999, 5, 5), result.getDateOfBirth());
        assertEquals("Nurlan", result.getFirstName());
        assertTrue(result.isActive());
    }

    @Test
    void updateTrainee_keepsExistingAddressWhenDtoAddressIsNull() {
        UpdateTraineeDto dto = new UpdateTraineeDto();
        dto.setUsername("Nurlan.Bekov");
        dto.setIsActive(true);
        when(userService.updateUser("Nurlan.Bekov", dto, true)).thenReturn(user);
        when(traineeRepository.findTraineeByUserId(1L)).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        TraineeTrainersListDto result = traineeService.updateTrainee(dto, "Nurlan.Bekov");

        assertEquals("Bishkek", result.getAddress());
        assertEquals(LocalDate.of(2000, 1, 1), result.getDateOfBirth());
    }

    @Test
    void updateTrainee_nullDto_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.updateTrainee(null, "Nurlan.Bekov"));
        verifyNoInteractions(userService);
    }

    @Test
    void updateTrainee_futureDateOfBirth_throwsDateInvalidException() {
        UpdateTraineeDto dto = new UpdateTraineeDto();
        dto.setUsername("Nurlan.Bekov");
        dto.setDateOfBirth(LocalDate.now().plusDays(1));
        dto.setIsActive(true);

        assertThrows(DateInvalidException.class, () -> traineeService.updateTrainee(dto, "Nurlan.Bekov"));
        verifyNoInteractions(userService);
    }

    @Test
    void updateTrainee_usernameMismatch_throwsAuthorizationException() {
        UpdateTraineeDto dto = new UpdateTraineeDto();
        dto.setUsername("Someone.Else");
        dto.setIsActive(true);

        assertThrows(AuthorizationException.class, () -> traineeService.updateTrainee(dto, "Nurlan.Bekov"));
        verifyNoInteractions(userService);
    }

    @Test
    void updateTrainee_traineeProfileMissing_throwsNoSuchElementException() {
        UpdateTraineeDto dto = new UpdateTraineeDto();
        dto.setUsername("Nurlan.Bekov");
        dto.setIsActive(true);
        when(userService.updateUser("Nurlan.Bekov", dto, true)).thenReturn(user);
        when(traineeRepository.findTraineeByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> traineeService.updateTrainee(dto, "Nurlan.Bekov"));
    }

    @Test
    void findByUsername_success_returnsTrainee() {
        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
        when(traineeRepository.findTraineeByUserId(1L)).thenReturn(Optional.of(trainee));

        TraineeTrainersListDto result = traineeService.findByUsername("Nurlan.Bekov");

        assertEquals("Bishkek", result.getAddress());
        assertEquals("Nurlan", result.getFirstName());
        assertTrue(result.isActive());
    }

    @Test
    void findByUsername_blankUsername_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.findByUsername("  "));
        verifyNoInteractions(userService);
    }

    @Test
    void findByUsername_noTraineeProfile_throwsNoSuchElementException() {
        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
        when(traineeRepository.findTraineeByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> traineeService.findByUsername("Nurlan.Bekov"));
    }

    @Test
    void changeStatusTrainer_success_delegatesToUserService() {
        ChangeStatusDto dto = new ChangeStatusDto();
        dto.setUsername("Nurlan.Bekov");
        dto.setIsActive(false);
        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
        when(traineeRepository.findTraineeByUserId(1L)).thenReturn(Optional.of(trainee));

        traineeService.changeStatusTrainer(dto, "Nurlan.Bekov");

        verify(userService).changeStatus(user, false);
    }

    @Test
    void changeStatusTrainer_authorizationMismatch_throwsAuthorizationException() {
        ChangeStatusDto dto = new ChangeStatusDto();
        dto.setUsername("Someone.Else");
        dto.setIsActive(false);

        assertThrows(AuthorizationException.class, () -> traineeService.changeStatusTrainer(dto, "Nurlan.Bekov"));
        verifyNoInteractions(userService);
    }

    @Test
    void changeStatusTrainer_noTraineeProfile_throwsBeforeChangingStatus() {
        ChangeStatusDto dto = new ChangeStatusDto();
        dto.setUsername("Nurlan.Bekov");
        dto.setIsActive(false);
        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
        when(traineeRepository.findTraineeByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> traineeService.changeStatusTrainer(dto, "Nurlan.Bekov"));
        verify(userService, never()).changeStatus(any(), any());
    }

    @Test
    void getTrainingsByCriteria_success_mapsTrainings() {
        TraineeTrainingsDto criteria = new TraineeTrainingsDto("Nurlan.Bekov",
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(2), null, null);

        Trainee traineeWithUser = new Trainee();
        User trainerLikeUser = new User();
        trainerLikeUser.setFirstName("Akhmat");
        traineeWithUser.setUser(trainerLikeUser);

        TrainingType type = new TrainingType("individual");
        Training training = new Training();
        training.setTrainee(traineeWithUser);
        training.setTrainingType(type);
        training.setTrainingName("Cardio");
        training.setDate(LocalDate.now());
        training.setDuration(45);

        when(trainingRepository.findAll(any(Specification.class))).thenReturn(List.of(training));

        List<TrainingsDisplayInfoTrainee> result = traineeService.getTrainingsByCriteria(criteria);

        assertEquals(1, result.size());
        assertEquals("Cardio", result.get(0).getTrainingName());
        assertEquals("individual", result.get(0).getTrainingType());
        assertEquals(45, result.get(0).getTrainingDuration());
    }

    @Test
    void deleteTraineeByUsername_success_deletesTrainee() {
        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
        when(traineeRepository.findTraineeByUserId(1L)).thenReturn(Optional.of(trainee));

        traineeService.deleteTraineeByUsername("Nurlan.Bekov", "Nurlan.Bekov");

        verify(traineeRepository).deleteById(trainee.getId());
    }

    @Test
    void deleteTraineeByUsername_authorizationMismatch_throwsAuthorizationException() {
        assertThrows(AuthorizationException.class,
                () -> traineeService.deleteTraineeByUsername("Nurlan.Bekov", "Someone.Else"));
        verifyNoInteractions(userService, traineeRepository);
    }

    @Test
    void deleteTraineeByUsername_noTraineeProfile_throwsNoSuchElementException() {
        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
        when(traineeRepository.findTraineeByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> traineeService.deleteTraineeByUsername("Nurlan.Bekov", "Nurlan.Bekov"));
        verify(traineeRepository, never()).deleteById(any());
    }

    @Test
    void findNotAssignedTrainersByUsername_success_returnsMappedList() {
        User trainerUser = new User();
        trainerUser.setFirstName("Bekzat");
        trainerUser.setLastName("Isakov");
        trainerUser.setUsername("Bekzat.Isakov");
        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setSpecialization(new TrainingType("group"));

        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
        when(traineeRepository.findTraineeByUserUsername("Nurlan.Bekov")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findNotAssignedTrainees(trainee.getId())).thenReturn(List.of(trainer));

        List<TrainerUsernameDto> result = traineeService.findNotAssignedTrainersByUsername("Nurlan.Bekov");

        assertEquals(1, result.size());
        assertEquals("Bekzat.Isakov", result.get(0).getUsername());
        assertEquals("group", result.get(0).getSpecialization());
    }

    @Test
    void findNotAssignedTrainersByUsername_nullUsername_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> traineeService.findNotAssignedTrainersByUsername(null));
        verifyNoInteractions(userService);
    }

    @Test
    void findNotAssignedTrainersByUsername_noTraineeProfile_throwsNoSuchElementException() {
        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
        when(traineeRepository.findTraineeByUserUsername("Nurlan.Bekov")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> traineeService.findNotAssignedTrainersByUsername("Nurlan.Bekov"));
    }

    @Test
    void updateTrainersByUsername_addsNewTrainer() {
        Trainer trainer = new Trainer();
        User trainerUser = new User();
        trainerUser.setId(5L);
        trainerUser.setUsername("Bekzat.Isakov");
        trainerUser.setFirstName("Bekzat");
        trainerUser.setLastName("Isakov");
        trainer.setUser(trainerUser);
        trainer.setSpecialization(new TrainingType("group"));
        trainer.setTrainees(new ArrayList<>());

        UpdateTraineeTrainersDto dto = new UpdateTraineeTrainersDto("Nurlan.Bekov", List.of("Bekzat.Isakov"));
        when(traineeRepository.findTraineeByUserUsername("Nurlan.Bekov")).thenReturn(Optional.of(trainee));
        when(userService.findUserByUsername("Bekzat.Isakov")).thenReturn(trainerUser);
        when(trainerRepository.findByUserId(5L)).thenReturn(Optional.of(trainer));

        List<TrainerUsernameDto> result = traineeService.updateTrainersByUsername(dto, "Nurlan.Bekov");

        assertTrue(trainer.getTrainees().contains(trainee));
        assertEquals(1, result.size());
        assertEquals("Bekzat.Isakov", result.get(0).getUsername());
        assertEquals("group", result.get(0).getSpecialization());
    }

    @Test
    void updateTrainersByUsername_removesTrainerNotInNewList() {
        Trainer trainer = new Trainer();
        User trainerUser = new User();
        trainerUser.setUsername("OldTrainer.Name");
        trainer.setUser(trainerUser);
        trainer.setTrainees(new ArrayList<>(List.of(trainee)));
        trainee.setTrainers(new ArrayList<>(List.of(trainer)));

        UpdateTraineeTrainersDto dto = new UpdateTraineeTrainersDto("Nurlan.Bekov", List.of());
        when(traineeRepository.findTraineeByUserUsername("Nurlan.Bekov")).thenReturn(Optional.of(trainee));

        List<TrainerUsernameDto> result = traineeService.updateTrainersByUsername(dto, "Nurlan.Bekov");

        assertFalse(trainer.getTrainees().contains(trainee));
        assertTrue(result.isEmpty());
        verifyNoInteractions(userService);
    }

    @Test
    void updateTrainersByUsername_authorizationMismatch_throwsAuthorizationException() {
        UpdateTraineeTrainersDto dto = new UpdateTraineeTrainersDto("Someone.Else", List.of());

        assertThrows(AuthorizationException.class,
                () -> traineeService.updateTrainersByUsername(dto, "Nurlan.Bekov"));
        verifyNoInteractions(traineeRepository, userService, trainerRepository);
    }

    @Test
    void updateTrainersByUsername_traineeNotFound_throwsNoSuchElementException() {
        UpdateTraineeTrainersDto dto = new UpdateTraineeTrainersDto("Nurlan.Bekov", List.of());
        when(traineeRepository.findTraineeByUserUsername("Nurlan.Bekov")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> traineeService.updateTrainersByUsername(dto, "Nurlan.Bekov"));
    }

    @Test
    void updateTrainersByUsername_unknownTrainerUsername_throwsNoSuchElementException() {
        User trainerUser = new User();
        trainerUser.setId(5L);
        trainerUser.setUsername("Ghost.Trainer");

        UpdateTraineeTrainersDto dto = new UpdateTraineeTrainersDto("Nurlan.Bekov", List.of("Ghost.Trainer"));
        when(traineeRepository.findTraineeByUserUsername("Nurlan.Bekov")).thenReturn(Optional.of(trainee));
        when(userService.findUserByUsername("Ghost.Trainer")).thenReturn(trainerUser);
        when(trainerRepository.findByUserId(5L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> traineeService.updateTrainersByUsername(dto, "Nurlan.Bekov"));
    }
}
