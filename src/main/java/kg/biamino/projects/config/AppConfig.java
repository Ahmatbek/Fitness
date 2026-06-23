package kg.biamino.projects.config;

import com.google.gson.reflect.TypeToken;
import kg.biamino.projects.memory.InMemoryStorage;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan("kg.biamino.projects")
@PropertySource("classpath:application.properties")
public class AppConfig {
    @Value("${trainee}")
    private String trainee;

    @Value("${training}")
    private String training;

    @Value("${trainer}")
    private String trainer;

    @Value("${users}")
    private String users;

    @Bean
    public Map<String, Training> training() {
        return new InMemoryStorage().readTrainee(training,new TypeToken<List<Training>>() {}.getType(), item -> String.valueOf(item.getTrainingName()));
    }

    @Bean
    public Map<String, Trainer> trainers() {
        return new InMemoryStorage().readTrainee(trainer,new TypeToken<List<Trainer>>() {}.getType(), item -> String.valueOf(item.getUserId()));
    }

    @Bean
    public Map<String, Trainee> trainees() {
        return new InMemoryStorage().readTrainee(trainee,new TypeToken<List<Trainee>>() {}.getType(), item -> String.valueOf(item.getUserId()));
    }

    @Bean
    public Map<String, User> users() {
        return new InMemoryStorage().readTrainee(users, new TypeToken<List<User>>() {}.getType(), item -> String.valueOf(item.getUsername()));
    }



}
