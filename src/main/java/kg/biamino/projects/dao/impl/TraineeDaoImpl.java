package kg.biamino.projects.dao.impl;

import kg.biamino.projects.dao.TraineeDao;
import kg.biamino.projects.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TraineeDaoImpl implements TraineeDao {

    @Autowired
    private Map<String, Trainee> trainees;

    @Override
    public Trainee getTrainee(String userId) {
        return trainees.get(userId);
    }

    @Override
    public Trainee updateTrainee(String userId, Trainee trainee) {
        return trainees.computeIfAbsent(userId, id -> trainee);
    }

    @Override
    public void deleteTrainee(String userId) {
        trainees.remove(userId);
    }

    @Override
    public Trainee createTrainee(String userId, Trainee trainee) {
        return trainees.put(userId, trainee);
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return new ArrayList<>(trainees.values());
    }
}
