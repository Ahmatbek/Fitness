//package service;
//
//import kg.biamino.projects.dto.AuthUserDto;
//import kg.biamino.projects.dto.TraineeDto;
//import kg.biamino.projects.dto.TraineeStatusChangeDto;
//import kg.biamino.projects.dto.TrainerDto;
//import kg.biamino.projects.dto.TrainingDto;
//import kg.biamino.projects.model.Trainee;
//import kg.biamino.projects.model.Trainer;
//import kg.biamino.projects.model.Training;
//import kg.biamino.projects.model.TrainingType;
//import kg.biamino.projects.records.ProfilePasswordChange;
//import kg.biamino.projects.records.TraineeCriteriaDto;
//import kg.biamino.projects.records.TrainerCriteriaDto;
//import kg.biamino.projects.service.TraineeService;
//import kg.biamino.projects.service.TrainerService;
//import kg.biamino.projects.service.TrainingService;
//import kg.biamino.projects.service.TrainingTypeService;
//import kg.biamino.projects.service.impl.FitnessFacadeImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class FitnessFacadeTest {
//
//    @Mock
//    private TraineeService traineeService;
//    @Mock
//    private TrainerService trainerService;
//    @Mock
//    private TrainingService trainingService;
//    @Mock
//    private TrainingTypeService trainingTypeService;
//
//    private FitnessFacadeImpl facade;
//
//    private final AuthUserDto authUserDto = new AuthUserDto("Nurlan.Bekov", "pass123");
//
//    @BeforeEach
//    void setUp() {
//        facade = new FitnessFacadeImpl(traineeService, trainerService, trainingService, trainingTypeService);
//    }
//
//    @Test
//    void createTrainer_delegatesToTrainerService() {
//        TrainerDto dto = new TrainerDto("Aidana", "Toktosunova", "individual");
//        Trainer trainer = new Trainer();
//        when(trainerService.createTrainer(dto)).thenReturn(trainer);
//
//        assertEquals(trainer, facade.createTrainer(dto));
//    }
//
//    @Test
//    void createTrainee_delegatesToTraineeService() {
//        TraineeDto dto = new TraineeDto("Nurlan", "Bekov", "Bishkek", LocalDate.of(2000, 1, 1));
//        Trainee trainee = new Trainee();
//        when(traineeService.createTrainee(dto)).thenReturn(trainee);
//
//        assertEquals(trainee, facade.createTrainee(dto));
//    }
//
//    @Test
//    void getAllTrainers_delegatesToTrainerService() {
//        List<Trainer> trainers = List.of(new Trainer());
//        when(trainerService.getAllTrainers()).thenReturn(trainers);
//
//        assertEquals(trainers, facade.getAllTrainers());
//    }
//
//    @Test
//    void getAllTrainees_delegatesToTraineeService() {
//        List<Trainee> trainees = List.of(new Trainee());
//        when(traineeService.getAllTrainees()).thenReturn(trainees);
//
//        assertEquals(trainees, facade.getAllTrainees());
//    }
//
//    @Test
//    void getAllTrainings_delegatesToTrainingService() {
//        List<Training> trainings = List.of(new Training());
//        when(trainingService.getAllTrainings()).thenReturn(trainings);
//
//        assertEquals(trainings, facade.getAllTrainings());
//    }
//
//    @Test
//    void createTraining_delegatesToTrainingService() {
//        TrainingDto dto = new TrainingDto();
//        Training training = new Training();
//        when(trainingService.createTraining(authUserDto, dto)).thenReturn(training);
//
//        assertEquals(training, facade.createTraining(authUserDto, dto));
//    }
//
//    @Test
//    void getTraineeByUsername_delegatesToTraineeService() {
//        Trainee trainee = new Trainee();
//        when(traineeService.findByUsername(authUserDto)).thenReturn(trainee);
//
//        assertEquals(trainee, facade.getTraineeByUsername(authUserDto));
//    }
//
//    @Test
//    void getTrainerByUsername_delegatesToTrainerService() {
//        Trainer trainer = new Trainer();
//        when(trainerService.findByUsername(authUserDto)).thenReturn(trainer);
//
//        assertEquals(trainer, facade.getTrainerByUsername(authUserDto));
//    }
//
//    @Test
//    void passwordChangeTrainer_delegatesToTrainerService() {
//        ProfilePasswordChange change = new ProfilePasswordChange("Aidana.Toktosunova", "new", "old");
//        Trainer trainer = new Trainer();
//        when(trainerService.passwordChange(change)).thenReturn(trainer);
//
//        assertEquals(trainer, facade.passwordChangeTrainer(change));
//    }
//
//    @Test
//    void passwordChangeTrainee_delegatesToTraineeService() {
//        ProfilePasswordChange change = new ProfilePasswordChange("Nurlan.Bekov", "new", "old");
//        Trainee trainee = new Trainee();
//        when(traineeService.passwordChange(change)).thenReturn(trainee);
//
//        assertEquals(trainee, facade.passwordChangeTrainee(change));
//    }
//
//    @Test
//    void changeStatusTrainee_delegatesToTraineeService() {
//        TraineeStatusChangeDto statusChangeDto = new TraineeStatusChangeDto(authUserDto, false);
//
//        facade.changeStatusTrainee(statusChangeDto);
//
//        org.mockito.Mockito.verify(traineeService).changeStatusTrainee(statusChangeDto);
//    }
//
//    @Test
//    void changeStatusTrainer_delegatesToTrainerService() {
//        TraineeStatusChangeDto statusChangeDto = new TraineeStatusChangeDto(authUserDto, false);
//
//        facade.changeStatusTrainer(statusChangeDto);
//
//        org.mockito.Mockito.verify(trainerService).changeStatusTrainer(statusChangeDto);
//    }
//
//    @Test
//    void findTrainingTypeByName_delegatesToTrainingTypeService() {
//        TrainingType type = new TrainingType("individual");
//        when(trainingTypeService.findByName("individual")).thenReturn(type);
//
//        assertEquals(type, facade.findTrainingTypeByName("individual"));
//    }
//
//    @Test
//    void getTrainingsByTraineeUsernameAndCriteria_delegatesToTraineeService() {
//        TraineeCriteriaDto criteria = new TraineeCriteriaDto(1L, "Akhmat", "individual",
//                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
//        List<Training> trainings = List.of(new Training());
//        when(traineeService.getTrainingsByCriteria(authUserDto, criteria)).thenReturn(trainings);
//
//        assertEquals(trainings, facade.getTrainingsByTraineeUsernameAndCriteria(authUserDto, criteria));
//    }
//
//    @Test
//    void getTrainingsByTrainerUsernameAndCriteria_delegatesToTrainerService() {
//        TrainerCriteriaDto criteria = new TrainerCriteriaDto(1L, "Dilmurod",
//                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
//        List<Training> trainings = List.of(new Training());
//        when(trainerService.getTrainingsByCriteria(authUserDto, criteria)).thenReturn(trainings);
//
//        assertEquals(trainings, facade.getTrainingsByTrainerUsernameAndCriteria(authUserDto, criteria));
//    }
//
//    @Test
//    void getTrainersNotAssignedToTrainee_delegatesToTraineeService() {
//        List<Trainer> trainers = List.of(new Trainer());
//        when(traineeService.getTrainersNotAssignedToTrainee(authUserDto)).thenReturn(trainers);
//
//        assertEquals(trainers, facade.getTrainersNotAssignedToTrainee(authUserDto));
//    }
//
//    @Test
//    void updateTraineeTrainersList_delegatesToTrainerService() {
//        List<TrainerDto> dtos = List.of(new TrainerDto(1L, "Aidana", "Toktosunova", "individual"));
//        List<Trainer> trainers = List.of(new Trainer());
//        when(trainerService.updateTraineeTrainersList(authUserDto, dtos)).thenReturn(trainers);
//
//        assertEquals(trainers, facade.updateTraineeTrainersList(authUserDto, dtos));
//    }
//}
