package kg.biamino.projects.config;

import kg.biamino.projects.memory.InMemoryStorage;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Configuration
@ComponentScan("kg.biamino.projects")
@PropertySource("classpath:application.properties")
public class AppConfig {

    private InMemoryStorage storage;

    @Autowired
    public void setStorage(final InMemoryStorage storage) {
        this.storage=storage;
    }

    @Bean
    public Map<String, Training> training() {
        return storage.getTrainings();
    }

    @Bean
    public Map<String, Trainer> trainers() {
        return storage.getTrainers();
    }

    @Bean
    public Map<String, Trainee> trainees() {
        return storage.getTrainees();
    }

    @Bean
    public Map<String, User> users() {
        return storage.getUsers();
    }



}
