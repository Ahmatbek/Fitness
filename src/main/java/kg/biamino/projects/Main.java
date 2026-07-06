package kg.biamino.projects;

import kg.biamino.projects.config.AppConfig;
import kg.biamino.projects.dto.*;
import kg.biamino.projects.records.ProfilePasswordChange;
import kg.biamino.projects.records.TraineeCriteriaDto;
import kg.biamino.projects.service.FitnessFacade;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        FitnessFacade fitnessFacade = context.getBean(FitnessFacade.class);

        TrainerDto trainerDto = new TrainerDto("Akhmat","Tursunbaev", "group");
        TraineeDto traineeDto = new TraineeDto("Dilmurod","Sakhajinov","Bishkek", LocalDate.of(2005,3,3));
        AuthUserDto authUserDto = new AuthUserDto("Akhmat.Tursunbaev","giJdK_BQvC");
        TrainingDto trainingDto = new TrainingDto(2L,1L,"session-123","individual", LocalDate.now().plusDays(1), 90);
        ProfilePasswordChange profilePasswordChange = new ProfilePasswordChange("Dilmurod.Sakhajinov", "jdksjsss", "giJdK_BQvC");
//        TrainingType trainingType = new TrainingType("invidual");
//        TrainingDto trainingDto = new TrainingDto(1L,2L,"session-123", trainingType, LocalDate.now(),90);

        //create
//        fitnessFacade.createTrainer(trainerDto);
//        fitnessFacade.createTrainee(traineeDto);
//        fitnessFacade.createTrainer(trainerDto);

        //change password
//        fitnessFacade.passwordChangeTrainee(profilePasswordChange);

        //select by username
//        fitnessFacade.getTraineeByUsername(authUserDto);
//        System.out.println( fitnessFacade.getTrainerByUsername(authUserDto));


        //update
//        TrainerDto trainerDtoUpdate = new TrainerDto("Tilek","Toktobaev");
//        fitnessFacade.updateTrainer(authUserDto, trainerDtoUpdate);
//
//        TraineeDto traineeDtoUpdate= new TraineeDto("Sandy","Watson","New-York", LocalDate.of(2007,3,3));
//        fitnessFacade.updateTrainee(authUserDto,traineeDtoUpdate);


        //active-deactivate
//        TraineeStatusChangeDto statusChangeDto = new TraineeStatusChangeDto(authUserDto,true);
//        fitnessFacade.changeStatusTrainee(statusChangeDto);
//        TraineeStatusChangeDto statusChangeDto1 = new TraineeStatusChangeDto(authUserDto,false);
//        fitnessFacade.changeStatusTrainer(statusChangeDto1);

//        fitnessFacade.createTraining(trainingDto);

//        //getAll
//        System.out.println(fitnessFacade.getAllTrainers());
//        System.out.println(fitnessFacade.getAllTrainings());
//        System.out.println(fitnessFacade.getAllTrainees());
//
//        //getById
//        fitnessFacade.getTraineeByUserId(1L);
//        fitnessFacade.getTrainerByUserId(2L);
//        fitnessFacade.getTrainingByName("session-111");


//      fitnessFacade.createTraining(authUserDto,trainingDto);

        //14. Get Trainee Trainings List by trainee username and criteria (from date, to date, trainer
        //name, training type).


        AuthUserDto traineeAuth = new AuthUserDto("Dilmurod.Sakhajinov","S.I5T')1?&");
        System.out.println(fitnessFacade.getTrainingsByTraineeUsernameAndCriteria(traineeAuth, new TraineeCriteriaDto(2L, "Akhmat","individual", LocalDate.now().minusDays(2), LocalDate.now().plusDays(1))));













    }
}