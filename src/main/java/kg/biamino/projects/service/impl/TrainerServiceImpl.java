package kg.biamino.projects.service.impl;

import kg.biamino.projects.dto.AuthUserDto;
import kg.biamino.projects.dto.TrainerDto;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.User;
import kg.biamino.projects.records.ProfilePasswordChange;
import kg.biamino.projects.repository.TrainerRepository;
import kg.biamino.projects.service.TrainerService;
import kg.biamino.projects.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import javax.security.auth.login.CredentialException;
import javax.security.auth.login.CredentialNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;

import static kg.biamino.projects.utils.ValidationInput.nullChecker;
import static kg.biamino.projects.utils.ValidationInput.stringChecker;

@Service
@Slf4j
public class TrainerServiceImpl implements TrainerService {

    private TrainerRepository trainerRepository;

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setTrainerDao(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Override
    public Trainer getTrainerById(Long id) {
        log.info("Getting trainer with id {}", id);
        return trainerRepository.findById(id).orElse(null);
    }

    @Override
    public List<Trainer> getAllTrainers() {
        log.info("Getting all trainers");
        return trainerRepository.findAll();
    }

    @Override
    @Transactional
    public Trainer createTrainer(TrainerDto trainerDto) {
        nullChecker(trainerDto, "trainerDto");
        User user = userService.createUser(trainerDto);

        nullChecker(trainerDto.getSpecialization(), "specialization");

        Trainer trainer = new Trainer();
        trainer.setSpecialization(trainerDto.getSpecialization());
        trainer.setSpecialization(trainerDto.getSpecialization());
        trainer.setUser(user);

        log.info("Creating trainer with id {}", user.getId());
        return trainerRepository.save( trainer);
    }

    @Override
    public Trainer updateTrainer(String username, TrainerDto trainerDto) {
//        log.info("Updating trainer with username {}", username);
//        nullChecker(trainerDto, "trainerDto");
//        stringChecker(username, "firstName");
//        User user = userService.updateUsersName(username, trainerDto);
//        Trainer trainer = trainerDao.getTrainer(String.valueOf(user.getId()));
//        nullChecker(trainer, "Trainer");
//        trainer.setSpecialization(trainerDto.getSpecialization()!=null ? trainerDto.getSpecialization() : trainer.getSpecialization());
//
//        return trainerDao.updateTrainer(String.valueOf(user.getId()), trainer);

        return null;
    }

    @Override
    public Trainer findByUsername(AuthUserDto authUserDto) throws AuthenticationException{
       if(userService.userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword())){
           User user = userService.findUserByUsername(authUserDto.getUsername());
          return trainerRepository.findByUserId(user.getId()).orElseThrow(()-> new NoSuchElementException("Trainer not found with userId"+ user.getId()));
       }
       throw new AuthenticationException("Authentication failed " + authUserDto.getUsername());
    }

    @Override
    public Trainer passwordChange(ProfilePasswordChange profilePasswordChange) throws AuthenticationException {
        if(userService.userAuthenticated(profilePasswordChange.username(), profilePasswordChange.oldPassword())){
            User user = userService.findUserByUsername(profilePasswordChange.username());
            userService.changePassword(user, profilePasswordChange.newPassword());
            return trainerRepository.findByUserId(user.getId()).orElseThrow(()-> new NoSuchElementException("Trainer not found with userId"+ user.getId()));
        }
        throw new AuthenticationException("Authentication failed"+profilePasswordChange.username());
    }

}
