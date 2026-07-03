package kg.biamino.projects;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import kg.biamino.projects.config.AppConfig;
import kg.biamino.projects.dto.*;
import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.records.ProfilePasswordChange;
import kg.biamino.projects.service.FitnessFacade;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        FitnessFacade fitnessFacade = context.getBean(FitnessFacade.class);

        TrainerDto trainerDto = new TrainerDto("Akhmat","Tursunbaev", new TrainingType("akhmatbek"));
        TraineeDto traineeDto = new TraineeDto("Dilmurod","Sakhajinov","Bishkek", LocalDate.of(2005,3,3));
        AuthUserDto authUserDto = new AuthUserDto("dd.Tursunbaev","n|q;yQ5rK(");
        ProfilePasswordChange profilePasswordChange = new ProfilePasswordChange("Akhmat.Tursunbaev", "jdksjsss", "Yy:s^Wx}MU");
//        TrainingType trainingType = new TrainingType("invidual");
//        TrainingDto trainingDto = new TrainingDto(1L,2L,"session-123", trainingType, LocalDate.now(),90);

        //create
//        fitnessFacade.createTrainer(trainerDto);
        fitnessFacade.createTrainee(traineeDto);
        fitnessFacade.createTrainer(trainerDto);

        fitnessFacade.getTraineeByUsername(authUserDto);
        fitnessFacade.getTrainerByUsername(authUserDto);








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














    }
}