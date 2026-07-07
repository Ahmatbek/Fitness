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



        AuthUserDto traineeAuth = new AuthUserDto("Dilmurod.Sakhajinov","S.I5T')1?&");
        System.out.println(fitnessFacade.getTrainingsByTraineeUsernameAndCriteria(traineeAuth, new TraineeCriteriaDto(2L, "Akhmat","individual", LocalDate.now().minusDays(2), LocalDate.now().plusDays(1))));













    }
}