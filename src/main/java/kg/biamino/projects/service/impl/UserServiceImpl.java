package kg.biamino.projects.service.impl;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import kg.biamino.projects.dto.ChangePasswordDto;
import kg.biamino.projects.dto.NewUserCredentials;
import kg.biamino.projects.dto.UserCredentialsDto;
import kg.biamino.projects.dto.UserDto;
import kg.biamino.projects.exception.AuthenticationException;
import kg.biamino.projects.exception.AuthorizationException;
import kg.biamino.projects.exception.UserNotFoundException;
import kg.biamino.projects.model.User;
import kg.biamino.projects.repository.UserRepository;
import kg.biamino.projects.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static kg.biamino.projects.utils.ValidationInput.nullChecker;
import static kg.biamino.projects.utils.ValidationInput.stringChecker;

@Service
@Slf4j
@Counted(value = "users.methods", description = "userService number of times each method is called")
@Timed(value = "users", description = "amount of time each method executes")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final SecureRandom random = new SecureRandom();

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Getting all users");
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public NewUserCredentials createUser(UserDto user) {
        String password = generateThePassword();

        nullChecker(user, "user");
        stringChecker(user.getFirstName(), "userDto");
        stringChecker(user.getLastName(), "userDto");
        User user1 = new User();
        user1.setFirstName(user.getFirstName());
        user1.setLastName(user.getLastName());
        user1.setUsername(generateUsername(user.getFirstName(), user.getLastName()));
        user1.setPassword(bCryptPasswordEncoder.encode(password));
        user1.setIsActive(true);
        log.info("Created user {}", user1);
        userRepository.save(user1);

       return new NewUserCredentials(user1, password);
    }

    private String generateThePassword() {
        String pool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
        char[] password = new char[10];
        for (int i = 0; i < password.length; i++) {
            int index = random.nextInt(pool.length());
            password[i] = (pool.charAt(index));
        }
        return String.valueOf(password);


    }

    private String generateUsername(String firstName, String lastName) {
        if (firstName == null || lastName == null || firstName.isBlank() || lastName.isBlank()) {
            throw new IllegalArgumentException("First and last name cannot be null");
        }
        StringBuilder username = new StringBuilder();
        username.append(firstName);
        username.append(".");
        username.append(lastName);
        if (userRepository.findUserByUsername(username.toString()).isPresent()) {
            int sequence = 1;
            String uniqueUsername;

            while (true) {
                uniqueUsername = username.toString() + sequence;
                if (userRepository.findUserByUsername(uniqueUsername).isPresent()) {
                    sequence++;
                } else {
                    break;
                }
            }

            return uniqueUsername;
        }
        return username.toString();

    }


    @Transactional
    @Override
    public User updateUser(String username, UserDto userDto, boolean status) {
        nullChecker(userDto, "userDto");
        stringChecker(username, "userDto");
        User user = userRepository.findUserByUsername(username).orElseThrow(NoSuchElementException::new);
        nullChecker(user, "user");
        user.setFirstName(userDto.getFirstName() != null ? userDto.getFirstName() : user.getFirstName());
        user.setLastName(userDto.getLastName() != null ? userDto.getLastName() : user.getLastName());
        user.setIsActive(status);
        log.info("Updated user {}", user.getFirstName());
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user {}", id);
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        log.info("Finding user {}", username);
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found" + username));
    }

    @Override
    @Transactional(readOnly = true)
    public void userAuthenticated(String username, String password) {
        User user = findUserByUsername(username);
        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) throw new AuthenticationException("Invalid password");

    }
    @Override
    @Transactional
    public void changePassword(ChangePasswordDto changePasswordDto, String authUsername) {
        stringChecker(changePasswordDto.newPassword(), "newPassword");
        userAuthenticated(changePasswordDto.username(), changePasswordDto.oldPassword());
        User user = findUserByUsername(changePasswordDto.username());
        if(!user.getUsername().equals(authUsername)) {
            throw new AuthorizationException("User cannot change other's password");
        }

        user.setPassword(bCryptPasswordEncoder.encode(changePasswordDto.newPassword()));
        userRepository.save(user);

    }

    @Transactional
    @Override
    public void changeStatus(User user, Boolean status) {
        nullChecker(status, "status");
        user.setIsActive(status);
        userRepository.save(user);

    }

}

