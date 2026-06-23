package kg.biamino.projects.dao.impl;

import kg.biamino.projects.dao.TrainingDao;
import kg.biamino.projects.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TrainingDaoImpl implements TrainingDao {

    @Autowired
    private Map<String, Training> trainings;

    @Override
    public Training getTraining(String name) {
        return trainings.get(name);
    }

    @Override
    public List<Training> getAllTrainings() {
        return new ArrayList<>(trainings.values());
    }

    @Override
    public Training createTraining(String name, Training training) {
        return trainings.put(name, training);
    }
}
