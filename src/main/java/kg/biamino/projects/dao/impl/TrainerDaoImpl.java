package kg.biamino.projects.dao.impl;

import kg.biamino.projects.dao.TrainerDao;
import kg.biamino.projects.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TrainerDaoImpl implements TrainerDao {

    private Map<String, Trainer> trainers;

    @Autowired
    public void setTrainers(Map<String, Trainer> trainers) {
        this.trainers = trainers;
    }

    @Override
    public Trainer getTrainer(String id) {
        return trainers.get(id);
    }

    @Override
    public Trainer createTrainer(String id, Trainer trainer) {
         trainers.put(id, trainer);
         return trainer;
    }

    @Override
    public Trainer updateTrainer(String id, Trainer trainer) {
         trainers.put(id, trainer);
         return trainer;
    }

    @Override
    public List<Trainer> getAllTrainers(){
        return new ArrayList<>(trainers.values());
    }

}
