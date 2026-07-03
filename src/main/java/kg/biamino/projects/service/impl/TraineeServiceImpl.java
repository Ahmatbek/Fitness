package kg.biamino.projects.service.impl;

import kg.biamino.projects.dto.AuthUserDto;
import kg.biamino.projects.dto.TraineeDto;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.User;
import kg.biamino.projects.records.ProfilePasswordChange;
import kg.biamino.projects.repository.TraineeRepository;
import kg.biamino.projects.service.TraineeService;
import kg.biamino.projects.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static kg.biamino.projects.utils.ValidationInput.stringChecker;


@Service
@Slf4j
public class TraineeServiceImpl implements TraineeService {

    private TraineeRepository traineeRepository;
    private UserService userService;


    @Autowired
    public void setTraineeDao(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public Trainee getTraineeById(Long id) {
        log.info("Getting trainee by id: {}", id);
        return traineeRepository.findById(id).orElseThrow(()-> new NoSuchElementException("Trainee with id " + id + " not found"));

    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainee> getAllTrainees() {
        log.info("Getting all trainees");
        return traineeRepository.findAll();
    }

    @Override
    @Transactional
    public Trainee createTrainee(TraineeDto traineeDto) {
        dateValidation(traineeDto);
        log.info("Creating trainee: {}", traineeDto);
        User user = userService.createUser(traineeDto);


        Trainee trainee = new Trainee();
        trainee.setAddress(traineeDto.getAddress());
        trainee.setDateOfBirth(traineeDto.getDateOfBirth());
        trainee.setUser(user);


        return traineeRepository.save(trainee);
    }

    @Override
    public Trainee updateTrainee(String username, TraineeDto traineeDto) {
//        dateValidation(traineeDto);
//        log.info("Updating trainee: {}", traineeDto);
//        User user = userService.updateUsersName(username, traineeDto);

//        Trainee trainee = traineeRepository.findById(user.getId());
//        nullChecker(trainee,"trainee");
//        trainee.setDateOfBirth(traineeDto.getDateOfBirth());
//        trainee.setAddress(traineeDto.getAddress() != null ? traineeDto.getAddress() : trainee.getAddress());

//        return traineeRepository.update(trainee);
        return null;
    }

    @Override
    @Transactional
    public void deleteTraineeById(Long id){
        traineeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Trainee findByUsername(AuthUserDto authUserDto) throws AuthenticationException {
        if(userService.userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword())){
            stringChecker(authUserDto.getUsername(), "username");
            User user = userService.findUserByUsername(authUserDto.getUsername());
            return traineeRepository.findByUsername(user.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid username"));
        }
        throw new AuthenticationException("Invalid username");
    }


    @Override
    public Trainee passwordChange(ProfilePasswordChange profilePasswordChange) throws AuthenticationException {
        if(userService.userAuthenticated(profilePasswordChange.username(), profilePasswordChange.oldPassword())){
            User user = userService.findUserByUsername(profilePasswordChange.username());
            userService.changePassword(user, profilePasswordChange.newPassword());
            return traineeRepository.findByUsername(user.getUsername()).orElseThrow(()-> new NoSuchElementException("Invalid username"));
        }
        throw new AuthenticationException("Authentication failed"+profilePasswordChange.username());
    }




    private void dateValidation(TraineeDto traineeDto) {
        if(traineeDto == null || traineeDto.getDateOfBirth() == null || traineeDto.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid date of birth");
        }
    }
}
