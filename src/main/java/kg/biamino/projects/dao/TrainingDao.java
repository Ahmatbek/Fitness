package kg.biamino.projects.dao;

import kg.biamino.projects.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface TrainingDao {

    Training getTraining(String name);

    List<Training> getAllTrainings();

    Training createTraining(String name, Training training);
}
