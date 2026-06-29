package kg.biamino.projects.memory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.PostConstruct;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.User;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


@Component
@Slf4j
@Getter
public class InMemoryStorage {

    @Value("${trainee}")
    private String trainee;

    @Value("${training}")
    private String training;

    @Value("${trainer}")
    private String trainer;

    @Value("${users}")
    private String user;

    private Map<String, Trainee> trainees;
    private Map<String, Trainer> trainers;
    private Map<String, Training> trainings;
    private Map<String, User> users;

    private static final Gson GSON =
            new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();


    @PostConstruct
    public void init() {
        trainees = getInputStreamMap(trainee, new TypeToken<List<Trainee>>() {}.getType(), e-> String.valueOf(e.getUserId()));
        trainers = getInputStreamMap(trainer, new TypeToken<List<Trainer>>() {}.getType(), e-> String.valueOf(e.getUserId()));
        trainings = getInputStreamMap(training, new TypeToken<List<Training>>() {}.getType(), Training::getTrainingName);
        users = getInputStreamMap(user, new TypeToken<List<User>>() {}.getType(), User::getUsername);
    }


    private <T> Map<String,T> getInputStreamMap(String path, Type type, Function<T, String> mapper) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("File not found: " + path);
            List<T> entities =  GSON.fromJson(new InputStreamReader(is), type);
            Map<String, T> local = new HashMap<>();
            entities.forEach(entity -> local.put(mapper.apply(entity), entity));
            return local;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }





}

