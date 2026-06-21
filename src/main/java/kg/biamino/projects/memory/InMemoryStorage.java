package kg.biamino.projects.memory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
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
public class InMemoryStorage {
    private static final Gson GSON =
            new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();


    public <T> Map<String, T> readTrainee(String path, Type type, Function<T, String> name) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("File not found: " + path);
            List<T> list = GSON.fromJson(new InputStreamReader(is),type);
            Map<String, T> map = new HashMap<>();
            list.forEach(item -> map.put(name.apply(item), item));
            return map;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}

