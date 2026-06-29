package kg.biamino.projects.service.impl;

import jakarta.annotation.PostConstruct;
import kg.biamino.projects.dao.UserDao;
import kg.biamino.projects.dto.UserDto;
import kg.biamino.projects.model.User;
import kg.biamino.projects.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static kg.biamino.projects.utils.ValidationInput.nullChecker;
import static kg.biamino.projects.utils.ValidationInput.stringChecker;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private UserDao userDao;

    private final AtomicLong counter = new AtomicLong();
    private final SecureRandom random = new SecureRandom();

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<User> getAllUsers(){
        log.info("Getting all users");
        return userDao.getAllUsers();
    }

    @Override
    public User createUser(UserDto user) {

        nullChecker(user, "user");
        stringChecker(user.getFirstName(), "userDto");
        stringChecker(user.getLastName(), "userDto");
        User user1 = new User();
        user1.setId(counter.incrementAndGet());
        user1.setFirstName(user.getFirstName());
        user1.setLastName(user.getLastName());
        user1.setUsername(generateUsername(user.getFirstName(), user.getLastName()));
        user1.setPassword(generateThePassword());
        user1.setIsActive(true);
        log.info("Created user {}", user1);
        return userDao.createUser(user1);
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
        if(userDao.getUserByUsername(username.toString())!=null) {
            int sequence = 1;
            String uniqueUsername;

            while(true){
                uniqueUsername = username.toString()+sequence;
                if(userDao.getUserByUsername(uniqueUsername)!=null) {
                    sequence++;
                }else{
                    break;
                }
            }

            return uniqueUsername;
        }
        return username.toString();

    }

    @PostConstruct
    public void init(){
        long maxUserId= 0L;
        List<User> userList = userDao.getAllUsers();
        for(User user: userList) {
            if(user.getId()==null || user.getId() <=0) {
                continue;
            }
            if(user.getId()>maxUserId){
                maxUserId= user.getId();
            }
        }
        counter.set(maxUserId);

    }

    @Override
    public User updateUsersName(String username, UserDto userDto){
        nullChecker(userDto, "userDto");
        User user = userDao.getUserByUsername(username);
        nullChecker(user, "user");
        user.setFirstName(userDto.getFirstName() != null ? userDto.getFirstName() : user.getFirstName());
        user.setLastName(userDto.getLastName() != null ? userDto.getLastName() : user.getLastName());
        user.setIsActive(true);
        log.info("Updated user {}", user.getFirstName());
        userDao.updateUser(user);
        return user;
    }

    @Override
    public void deleteUser(String username) {
        log.info("Deleting user {}", username);
        userDao.deleteUser(username);
    }

    @Override
    public User findUserByUsername(String username){
        log.info("Finding user {}", username);
        return userDao.getUserByUsername(username);
    }

}

