package kg.biamino.projects.service.impl;

import kg.biamino.projects.dao.TrainerDao;
import kg.biamino.projects.dto.TrainerDto;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.User;
import kg.biamino.projects.service.TrainerService;
import kg.biamino.projects.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static kg.biamino.projects.utils.ValidationInput.nullChecker;
import static kg.biamino.projects.utils.ValidationInput.stringChecker;

@Service
@Slf4j
public class TrainerServiceImpl implements TrainerService {

    private TrainerDao trainerDao;

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public Trainer getTrainer(Long id) {
        log.info("Getting trainer with id {}", id);
        return trainerDao.getTrainer(String.valueOf(id));
    }

    @Override
    public List<Trainer> getAllTrainers() {
        log.info("Getting all trainers");
        return trainerDao.getAllTrainers();
    }

    @Override
    public Trainer createTrainer(TrainerDto trainerDto) {
        nullChecker(trainerDto, "trainerDto");
        stringChecker(trainerDto.getFirstName(), "firstName");
        stringChecker(trainerDto.getLastName(), "lastName");
        User user = userService.createUser(trainerDto);

        stringChecker(trainerDto.getSpecialization(), "specializatoion");

        Trainer trainer = new Trainer();
        trainer.setSpecialization(trainerDto.getSpecialization()!=null?trainerDto.getSpecialization():"");
        trainer.setUserId(user.getId());

        log.info("Creating trainer with id {}", user.getId());
        return trainerDao.createTrainer(String.valueOf(user.getId()), trainer);
    }

    @Override
    public Trainer updateTrainer(String username, TrainerDto trainerDto) {
        log.info("Updating trainer with username {}", username);
        nullChecker(trainerDto, "trainerDto");
        stringChecker(username, "firstName");
        User user = userService.updateUsersName(username, trainerDto);
        Trainer trainer = trainerDao.getTrainer(String.valueOf(user.getId()));
        nullChecker(trainer, "Trainer");
        trainer.setSpecialization(trainerDto.getSpecialization()!=null ? trainerDto.getSpecialization() : trainer.getSpecialization());

        return trainerDao.updateTrainer(String.valueOf(user.getId()), trainer);
    }
}
