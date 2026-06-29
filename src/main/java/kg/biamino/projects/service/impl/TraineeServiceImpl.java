package kg.biamino.projects.service.impl;

import kg.biamino.projects.dao.TraineeDao;
import kg.biamino.projects.dto.TraineeDto;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.User;
import kg.biamino.projects.service.TraineeService;
import kg.biamino.projects.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static kg.biamino.projects.utils.ValidationInput.nullChecker;

@Service
@Slf4j
public class TraineeServiceImpl implements TraineeService {

    private TraineeDao traineeDao;
    private UserService userService;


    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

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
    public Trainee createTrainee(TraineeDto traineeDto) {
        dateValidation(traineeDto);
        log.info("Creating trainee: {}", traineeDto);
        User user = userService.createUser(traineeDto);


        Trainee trainee = new Trainee();
        trainee.setAddress(traineeDto.getAddress());
        trainee.setDateOfBirth(traineeDto.getDateOfBirth());
        trainee.setUserId(user.getId());

        return traineeDao.createTrainee(String.valueOf(user.getId()), trainee);
    }

    @Override
    public Trainee updateTrainee(String username, TraineeDto traineeDto) {
        dateValidation(traineeDto);
        log.info("Updating trainee: {}", traineeDto);
        User user = userService.updateUsersName(username, traineeDto);

        Trainee trainee = traineeDao.getTrainee(String.valueOf(user.getId()));
        nullChecker(trainee,"trainee");
        trainee.setDateOfBirth(traineeDto.getDateOfBirth());
        trainee.setAddress(traineeDto.getAddress() != null ? traineeDto.getAddress() : trainee.getAddress());

        return traineeDao.updateTrainee(String.valueOf(user.getId()), trainee);
    }

    @Override
    public void deleteTrainee(String username){
        log.info("Deleting trainee: {}", username);
        User user = userService.findUserByUsername(username);
        nullChecker(user,"username");
        traineeDao.deleteTrainee(String.valueOf(user.getId()));
        userService.deleteUser(username);
    }


    private void dateValidation(TraineeDto traineeDto) {
        if(traineeDto == null || traineeDto.getDateOfBirth() == null || traineeDto.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid date of birth");
        }
    }
}
