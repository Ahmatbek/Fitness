package kg.biamino.projects.service.impl;

import kg.biamino.projects.dao.TrainingDao;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TrainingServiceImpl implements TrainingService {
    @Autowired
    private TrainingDao trainingDao;

    @Override
    public Training getTrainingById(String name) {
        log.info("Getting training by name {}", name);
        return trainingDao.getTraining(name);
    }

    @Override
    public List<Training> getAllTrainings() {
        log.info("Getting all trainings");
        return trainingDao.getAllTrainings();
    }

    @Override
    public Training createTraining(String name, Training training){
        log.info("Creating training with name {}", name);
        return trainingDao.createTraining(name, training);
    }
}
