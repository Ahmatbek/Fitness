//package service;
//
//import kg.biamino.projects.dto.AuthUserDto;
//import kg.biamino.projects.dto.TraineeDto;
//import kg.biamino.projects.dto.TraineeStatusChangeDto;
//import kg.biamino.projects.model.Trainee;
//import kg.biamino.projects.model.Trainer;
//import kg.biamino.projects.model.Training;
//import kg.biamino.projects.model.User;
//import kg.biamino.projects.records.ProfilePasswordChange;
//import kg.biamino.projects.records.TraineeCriteriaDto;
//import kg.biamino.projects.repository.TraineeRepository;
//import kg.biamino.projects.repository.TrainerRepository;
//import kg.biamino.projects.repository.TrainingRepository;
//import kg.biamino.projects.service.UserService;
//import kg.biamino.projects.service.impl.TraineeServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TraineeServiceTest {
//
//    @Mock
//    private TraineeRepository traineeRepository;
//    @Mock
//    private TrainerRepository trainerRepository;
//    @Mock
//    private TrainingRepository trainingRepository;
//    @Mock
//    private UserService userService;
//
//    private TraineeServiceImpl traineeService;
//
//    private User user;
//    private Trainee trainee;
//    private final AuthUserDto authUserDto = new AuthUserDto("Nurlan.Bekov", "pass123");
//
//    @BeforeEach
//    void setUp() {
//        traineeService = new TraineeServiceImpl(userService);
//        traineeService.setTrainerRepository(trainerRepository);
//        traineeService.setTrainingRepository(trainingRepository);
//        traineeService.setTraineeRepository(traineeRepository);
//
//        user = new User();
//        user.setId(1L);
//        user.setUsername("Nurlan.Bekov");
//        user.setPassword("pass123");
//        user.setIsActive(true);
//
//        trainee = new Trainee();
//        trainee.setId(10L);
//        trainee.setUser(user);
//        trainee.setAddress("Bishkek");
//        trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
//    }
//
//    @Test
//    void getTraineeById_found_returnsTrainee() {
//        when(traineeRepository.findById(10L)).thenReturn(Optional.of(trainee));
//
//        assertEquals(trainee, traineeService.getTraineeById(10L));
//    }
//
//    @Test
//    void getTraineeById_notFound_throwsNoSuchElementException() {
//        when(traineeRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThrows(NoSuchElementException.class, () -> traineeService.getTraineeById(99L));
//    }
//
//    @Test
//    void getAllTrainees_returnsRepositoryResult() {
//        List<Trainee> trainees = List.of(trainee);
//        when(traineeRepository.findAll()).thenReturn(trainees);
//
//        assertEquals(trainees, traineeService.getAllTrainees());
//    }
//
//    @Test
//    void createTrainee_success_persistsWithUserAndAttributes() {
//        TraineeDto dto = new TraineeDto("Nurlan", "Bekov", "Bishkek", LocalDate.of(2000, 1, 1));
//        when(userService.createUser(dto)).thenReturn(user);
//        when(traineeRepository.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        Trainee result = traineeService.createTrainee(dto);
//
//        assertEquals("Bishkek", result.getAddress());
//        assertEquals(LocalDate.of(2000, 1, 1), result.getDateOfBirth());
//        assertEquals(user, result.getUser());
//    }
//
//    @Test
//    void createTrainee_dateOfBirthInFuture_throwsIllegalArgumentException() {
//        TraineeDto dto = new TraineeDto("Nurlan", "Bekov", "Bishkek", LocalDate.now().plusDays(1));
//
//        assertThrows(IllegalArgumentException.class, () -> traineeService.createTrainee(dto));
//        verifyNoInteractions(userService);
//    }
//
//    @Test
//    void createTrainee_dateOfBirthNull_isAllowed() {
//        TraineeDto dto = new TraineeDto("Nurlan", "Bekov", "Bishkek", null);
//        when(userService.createUser(dto)).thenReturn(user);
//        when(traineeRepository.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        Trainee result = assertDoesNotThrow(() -> traineeService.createTrainee(dto));
//
//        assertNull(result.getDateOfBirth());
//    }
//
//    @Test
//    void createTrainee_nullDto_throwsIllegalArgumentException() {
//        assertThrows(IllegalArgumentException.class, () -> traineeService.createTrainee(null));
//    }
//
//    @Test
//    void updateTrainee_success_updatesAddressAndDateOfBirth() {
//        TraineeDto dto = new TraineeDto("Nurlan", "Bekov", "Osh", LocalDate.of(1999, 5, 5));
//        when(userService.updateUser("Nurlan.Bekov", dto)).thenReturn(user);
//        when(traineeRepository.findByUserId(user.getId())).thenReturn(Optional.of(trainee));
//        when(traineeRepository.update(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        Trainee result = traineeService.updateTrainee(authUserDto, dto);
//
//        assertEquals("Osh", result.getAddress());
//        assertEquals(LocalDate.of(1999, 5, 5), result.getDateOfBirth());
//        verify(userService).userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword());
//    }
//
//    @Test
//    void updateTrainee_keepsExistingAddressWhenDtoAddressIsNull() {
//        TraineeDto dto = new TraineeDto("Nurlan", "Bekov", null, LocalDate.of(1999, 5, 5));
//        when(userService.updateUser("Nurlan.Bekov", dto)).thenReturn(user);
//        when(traineeRepository.findByUserId(user.getId())).thenReturn(Optional.of(trainee));
//        when(traineeRepository.update(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        Trainee result = traineeService.updateTrainee(authUserDto, dto);
//
//        assertEquals("Bishkek", result.getAddress());
//    }
//
//    @Test
//    void updateTrainee_traineeProfileMissing_throwsIllegalArgumentException() {
//        TraineeDto dto = new TraineeDto("Nurlan", "Bekov", "Osh", LocalDate.of(1999, 5, 5));
//        when(userService.updateUser("Nurlan.Bekov", dto)).thenReturn(user);
//        when(traineeRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
//
//        assertThrows(IllegalArgumentException.class, () -> traineeService.updateTrainee(authUserDto, dto));
//    }
//
//    @Test
//    void deleteTraineeById_delegatesToRepository() {
//        traineeService.deleteTraineeById(10L);
//        verify(traineeRepository).deleteById(10L);
//    }
//
//    @Test
//    void findByUsername_success_returnsTrainee() {
//        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
//        when(traineeRepository.findByUserId(user.getId())).thenReturn(Optional.of(trainee));
//
//        assertEquals(trainee, traineeService.findByUsername(authUserDto));
//    }
//
//    @Test
//    void findByUsername_noTraineeProfile_throwsNoSuchElementException() {
//        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
//        when(traineeRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
//
//        assertThrows(NoSuchElementException.class, () -> traineeService.findByUsername(authUserDto));
//    }
//
//    @Test
//    void passwordChange_success_delegatesToUserService() {
//        ProfilePasswordChange change = new ProfilePasswordChange("Nurlan.Bekov", "newPass", "pass123");
//        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
//        when(traineeRepository.findByUserId(user.getId())).thenReturn(Optional.of(trainee));
//
//        Trainee result = traineeService.passwordChange(change);
//
//        assertEquals(trainee, result);
//        verify(userService).changePassword(user, "newPass");
//    }
//
//    @Test
//    void passwordChange_blankNewPassword_throwsIllegalArgumentException() {
//        ProfilePasswordChange change = new ProfilePasswordChange("Nurlan.Bekov", "  ", "pass123");
//        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
//
//        assertThrows(IllegalArgumentException.class, () -> traineeService.passwordChange(change));
//        verify(userService, never()).changePassword(any(), any());
//    }
//
//    @Test
//    void changeStatusTrainee_success_delegatesToUserService() {
//        TraineeStatusChangeDto statusChangeDto = new TraineeStatusChangeDto(authUserDto, false);
//        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
//        when(traineeRepository.findByUserId(user.getId())).thenReturn(Optional.of(trainee));
//
//        traineeService.changeStatusTrainee(statusChangeDto);
//
//        verify(userService).changeStatus(user, false);
//    }
//
//    @Test
//    void changeStatusTrainee_noTraineeProfile_throwsBeforeChangingStatus() {
//        TraineeStatusChangeDto statusChangeDto = new TraineeStatusChangeDto(authUserDto, false);
//        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
//        when(traineeRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
//
//        assertThrows(NoSuchElementException.class, () -> traineeService.changeStatusTrainee(statusChangeDto));
//        verify(userService, never()).changeStatus(any(), any());
//    }
//
//    @Test
//    void removeTraineeByUsername_success_deletesTrainee() {
//        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
//        when(traineeRepository.findByUserId(user.getId())).thenReturn(Optional.of(trainee));
//
//        traineeService.removeTraineeByUsername(authUserDto);
//
//        verify(traineeRepository).deleteById(trainee.getId());
//    }
//
//    @Test
//    void removeTraineeByUsername_notFound_throwsNoSuchElementException() {
//        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
//        when(traineeRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
//
//        assertThrows(NoSuchElementException.class, () -> traineeService.removeTraineeByUsername(authUserDto));
//        verify(traineeRepository, never()).deleteById(any());
//    }
//
//    @Test
//    void getTrainingsByCriteria_success_delegatesWithUsername() {
//        TraineeCriteriaDto criteria = new TraineeCriteriaDto(1L, "Akhmat", "individual",
//                LocalDate.now().minusDays(2), LocalDate.now().plusDays(2));
//        List<Training> trainings = List.of(new Training());
//        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
//        when(trainingRepository.findByCriteria("Nurlan.Bekov", criteria)).thenReturn(trainings);
//
//        List<Training> result = traineeService.getTrainingsByCriteria(authUserDto, criteria);
//
//        assertEquals(trainings, result);
//    }
//
//    @Test
//    void getTrainersNotAssignedToTrainee_success_returnsList() {
//        List<Trainer> trainers = List.of(new Trainer());
//        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
//        when(traineeRepository.findByUsername("Nurlan.Bekov")).thenReturn(Optional.of(trainee));
//        when(trainerRepository.findNotAssignedTrainees(trainee.getId())).thenReturn(trainers);
//
//        List<Trainer> result = traineeService.getTrainersNotAssignedToTrainee(authUserDto);
//
//        assertEquals(trainers, result);
//    }
//
//    @Test
//    void getTrainersNotAssignedToTrainee_noTraineeProfile_throwsNoSuchElementException() {
//        when(userService.findUserByUsername("Nurlan.Bekov")).thenReturn(user);
//        when(traineeRepository.findByUsername("Nurlan.Bekov")).thenReturn(Optional.empty());
//
//        assertThrows(NoSuchElementException.class, () -> traineeService.getTrainersNotAssignedToTrainee(authUserDto));
//    }
//}
