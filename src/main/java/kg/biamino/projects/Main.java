package kg.biamino.projects;

import kg.biamino.projects.config.AppConfig;
import kg.biamino.projects.service.TraineeService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        TraineeService traineeService = context.getBean(TraineeService.class);

        System.out.println(traineeService.getAllTrainees());
        System.out.println(traineeService.getTraineeById(1L));

    }
}