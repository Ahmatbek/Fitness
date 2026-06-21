package kg.biamino.projects.service.impl;

import kg.biamino.projects.dao.TraineeDao;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.service.TraineeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TraineeServiceImpl implements TraineeService {

    @Autowired
    private TraineeDao traineeDao;

    @Override
    public Trainee getTraineeById(Long id) {
        log.info("Getting trainee by id: {}", id);
      return traineeDao.getTrainee(String.valueOf(id));

    }

    @Override
    public List<Trainee> getAllTrainees() {
        log.info("Getting all trainees");
        return traineeDao.getAllTrainees();
    }

    @Override
    public Trainee createTrainee(Long id, Trainee trainee){
        log.info("Creating trainee: {}", trainee);
        return traineeDao.createTrainee(String.valueOf(id), trainee);
    }

    @Override
    public Trainee updateTrainee(Long id, Trainee trainee){
        log.info("Updating trainee: {}", trainee);
        return traineeDao.updateTrainee(String.valueOf(id), trainee);
    }




}
