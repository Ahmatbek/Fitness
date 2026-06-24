package kg.biamino.projects;

import kg.biamino.projects.config.AppConfig;
import kg.biamino.projects.dto.TraineeDto;
import kg.biamino.projects.dto.TrainerDto;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.service.TraineeService;
import kg.biamino.projects.service.TrainerService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        TrainerDto trainerDto = new TrainerDto("Akhmat","Tursunbaev", "MMA");
        TrainerService trainerService = context.getBean(TrainerService.class);
        Trainer tra = trainerService.createTrainer(trainerDto);
        System.out.println(tra);

        TrainerDto trainerDto1 = new TrainerDto("Akhmad","Tursunbaev", "taekwondo");
        Trainer updated = trainerService.updateTrainer("Akhmat.Tursunbaev",trainerDto1);

        System.out.println(trainerService.getAllTrainers());


        TraineeDto traineeDto = new TraineeDto("Dilmurod","Sakhajinov","Bishkek", LocalDate.of(2005,3,3));
        TraineeService traineeService = context.getBean(TraineeService.class);
        traineeService.createTrainee(traineeDto);


    }
}