package kg.biamino.projects.service.impl;

import kg.biamino.projects.dto.UserDto;
import kg.biamino.projects.model.User;
import kg.biamino.projects.repository.UserRepository;
import kg.biamino.projects.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.security.SecureRandom;
import java.util.List;
import java.util.NoSuchElementException;

import static kg.biamino.projects.utils.ValidationInput.nullChecker;
import static kg.biamino.projects.utils.ValidationInput.stringChecker;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private final SecureRandom random = new SecureRandom();

    @Autowired
    public void setUserDao(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers(){
        log.info("Getting all users");
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User createUser(UserDto user) {

        nullChecker(user, "user");
        stringChecker(user.getFirstName(), "userDto");
        stringChecker(user.getLastName(), "userDto");
        User user1 = new User();
        user1.setFirstName(user.getFirstName());
        user1.setLastName(user.getLastName());
        user1.setUsername(generateUsername(user.getFirstName(), user.getLastName()));
        user1.setPassword(generateThePassword());
        user1.setIsActive(true);
        log.info("Created user {}", user1);
        return userRepository.save(user1);
    }

    private String generateThePassword(){
        String pool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
        char[] password = new char[10];
        for(int i=0; i< password.length; i++){
            int index = random.nextInt(pool.length());
            password[i]=(pool.charAt(index));
        }
        return String.valueOf(password);


    }

    private String generateUsername(String firstName, String lastName) {
        if(firstName == null || lastName == null || firstName.isBlank() || lastName.isBlank()) {
            throw new IllegalArgumentException("First and last name cannot be null");
        }
        StringBuilder username = new StringBuilder();
        username.append(firstName);
        username.append(".");
        username.append(lastName);
        if(userRepository.findUserByUsername(username.toString()).isPresent()){
            int sequence = 1;
            String uniqueUsername;

            while(true){
                uniqueUsername = username.toString()+sequence;
                if(userRepository.findUserByUsername(uniqueUsername).isPresent()) {
                    sequence++;
                }else{
                    break;
                }
            }

            return uniqueUsername;
        }
        return username.toString();

    }



    @Transactional
    @Override
    public User updateUser(String username, UserDto userDto){
        nullChecker(userDto, "userDto");
        stringChecker(username, "userDto");
        User user = userRepository.findUserByUsername(username).orElseThrow(NoSuchElementException::new);
        nullChecker(user, "user");
        user.setFirstName(userDto.getFirstName() != null ? userDto.getFirstName() : user.getFirstName());
        user.setLastName(userDto.getLastName() != null ? userDto.getLastName() : user.getLastName());
        user.setIsActive(true);
        log.info("Updated user {}", user.getFirstName());
        userRepository.update(user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user {}", id);
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByUsername(String username){
        log.info("Finding user {}", username);
        return userRepository.findUserByUsername(username).orElseThrow(()->new NoSuchElementException("User not found"+username));
    }

    @Override
    public void userAuthenticated(String username, String password){
        try{
            User user = findUserByUsername(username);
            if(!user.getPassword().equals(password)){
                throw new AuthenticationException("Passwords do not match");
            }
        }catch (NoSuchElementException | AuthenticationException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void changePassword(User user, String newPassword) {
        nullChecker(newPassword, "newPassword");
        stringChecker(user.getPassword(), "newPassword");
        user.setPassword(newPassword);
        userRepository.update(user);
    }

    @Transactional
    @Override
    public void changeStatus(User user, Boolean status){
        nullChecker(status, "status");
        user.setIsActive(status);
        userRepository.update(user);

    }

}

