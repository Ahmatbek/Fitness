import kg.biamino.projects.dao.UserDao;
import kg.biamino.projects.dto.UserDto;
import kg.biamino.projects.model.User;
import kg.biamino.projects.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserDao userDao;

    private Map<String, User> users;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        users = new HashMap<>();
        user = new User(1L, "Akhmatbek", "Tursunbaev", "Akhmatbek.Tursunbaev","qwertyasdfg", true);
        users.put(user.getUsername(), user);

        userDto =  new UserDto("Akhmatbek", "Tursunbaev");

    }



    @Test
    void createUser() {
        when(userDao.createUser(any())).thenReturn(user);

        User user1 = userService.createUser(userDto);

        assertNotNull(user1);
        assertEquals(user1.getUsername(), user.getUsername());
        assertEquals(user1.getLastName(), user.getLastName());
        verify(userDao, times(1)).createUser(any());
    }

    @Test
    void deleteUser() {

        userService.deleteUser(user.getUsername());

        verify(userDao, times(1)).deleteUser(any());

    }

    @Test
    void findUserByUsername() {
        when(userDao.getUserByUsername(any())).thenReturn(users.get(String.valueOf(user.getUsername())));

        User user1 = userService.findUserByUsername(user.getUsername());

        assertNotNull(user1);
        assertEquals(user, user1);
        verify(userDao,times(1)).getUserByUsername(any());
    }

    @Test
    void usernameGenerationAndPasswordCheck(){
        String username = user.getUsername()+1;
        User localUser =  new User();
        localUser.setUsername(username);
        localUser.setFirstName("Akhmatbek");
        localUser.setLastName("Tursunbaev");
        localUser.setId(2L);
        localUser.setIsActive(true);

        when(userDao.getUserByUsername(user.getUsername())).thenReturn(localUser);
        when(userDao.createUser(any())).thenAnswer(i -> i.<User>getArgument(0));
        User user = userService.createUser(userDto);

        assertNotNull(user);
        assertEquals(username,user.getUsername());
        assertEquals(user.getPassword().length(),10);
    }

    @Test
    void updateUser(){
        when(userDao.updateUser(any())).thenReturn(user);
        when(userDao.getUserByUsername(user.getUsername()))
                .thenReturn(users.get(user.getUsername()))
                .thenReturn(null);

        User user1 = userService.updateUsersName(user.getUsername(),userDto);

        assertNotNull(user1);
        assertEquals(userDto.getFirstName(), user1.getFirstName());
        assertEquals(userDto.getLastName(), user1.getLastName());
        assertEquals(user1.getUsername(), user.getUsername());
    }


    @Test
    void getAllUsers(){
        when(userDao.getAllUsers()).thenReturn(new ArrayList<>(users.values()));

        List<User> userList = userService.getAllUsers();

        assertNotNull(userList);
        assertEquals(users.size(),userList.size());
        verify(userDao,times(1)).getAllUsers();
    }











}
