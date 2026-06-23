package kg.biamino.projects.service.impl;

import jakarta.annotation.PostConstruct;
import kg.biamino.projects.dto.UserDto;
import kg.biamino.projects.model.User;
import kg.biamino.projects.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private Map<String, User> users;

    private AtomicInteger counter = new AtomicInteger();

    public UserServiceImpl() {}

    @Override
    public User createUser(UserDto user) {
        User user1 = new User();
        user1.setId((long)counter.incrementAndGet());
        user1.setFirstName(user.getFirstName());
        user1.setLastName(user.getLastName());
        user1.setUsername(generateUsername(user.getFirstName(), user.getLastName()));
        user1.setPassword(generateThePassword());
        users.put(user1.getUsername(), user1);
        return user1;
    }

    private String generateThePassword(){
        SecureRandom random = new SecureRandom();
        String pool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
        char[] password = new char[10];
        for(int i=0; i<10; i++){
            int index = random.nextInt(pool.length());
            password[i]=(pool.charAt(index));
        }
        return String.valueOf(password);

    }

    private String generateUsername(String firstName, String lastName) {
        if(firstName == null || lastName == null) {
            throw new IllegalArgumentException("First and last name cannot be null");
        }
        StringBuilder username = new StringBuilder();
        username.append(firstName);
        username.append(".");
        username.append(lastName);
        if(users.containsKey(username.toString())) {
            int sequence = 1;
            String uniqueUsername;

            while(true){
                uniqueUsername = username.toString()+sequence;
                if(users.containsKey(uniqueUsername)) {
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
        Long maxUserId= 0L;
        for(Map.Entry<String, User> entries : users.entrySet()){
            User user = entries.getValue();
            if(user.getId()>maxUserId){
                maxUserId= user.getId();
            }
        }
        counter=new AtomicInteger(maxUserId.intValue());

    }

    @Override
    public User updateUsersName(String username, UserDto userDto){
        if(userDto == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        User user = users.get(username);
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }
        user.setFirstName(userDto.getFirstName() != null ? userDto.getFirstName() : null);
        user.setLastName(userDto.getFirstName() != null ? userDto.getFirstName() : null);
        user.setUsername(generateUsername(user.getFirstName(), user.getLastName()));
        user.setPassword(generateThePassword());
        users.put(user.getUsername(), user);
        return user;
    }

}

