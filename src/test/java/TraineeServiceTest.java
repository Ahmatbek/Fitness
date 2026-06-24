import kg.biamino.projects.dao.TraineeDao;
import kg.biamino.projects.dto.TraineeDto;
import kg.biamino.projects.dto.UserDto;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.User;
import kg.biamino.projects.service.UserService;
import kg.biamino.projects.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Map<String, Trainee> trainees;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private UserService userService;

    private Trainee trainee;
    private TraineeDto traineeDto;
    private User user;

    @BeforeEach
    void setUp(){
        user = new User(1L, "Akhmatbek", "Tursunbaev", "Akhmatbek.Tursunbaev","qwertyasdfg", true);
        trainee = new Trainee(LocalDate.now(), "Bishkek", user.getId());
        traineeDto = new TraineeDto("Akhmatbek","Tursunbaev", "Bishkek", LocalDate.now());
//        userDto=new UserDto("Akhmatbek", "Tursunbaev");

        trainees = new HashMap<>();
        traineeService.setTraineeDao(traineeDao);
        trainees.put(user.getUsername(),trainee);


    }

    @Test
    void getAllTrainees() {
        when(traineeDao.getAllTrainees()).thenReturn(new ArrayList<>(trainees.values()));

        List<Trainee> traineeList = traineeService.getAllTrainees();

        assertNotNull(traineeList);
        assertEquals(1, traineeList.size());
    }

    @Test
    void getTraineeById() {
        when(traineeDao.getTrainee("1")).thenReturn(trainees.get(user.getUsername()));

        Trainee trainee = traineeService.getTraineeById(1L);

        assertNotNull(trainee);
        assertEquals(trainees.get(user.getUsername()), trainee);
    }

    @Test
    void addTrainee() {
        ArgumentCaptor<TraineeDto> argumentCaptor = ArgumentCaptor.forClass(TraineeDto.class);
        when(traineeDao.createTrainee(any(), any(Trainee.class))).thenReturn(trainee);
        when(userService.createUser(any())).thenReturn(user);

        Trainee trainee1 =  traineeService.createTrainee(traineeDto);

        verify(userService).createUser(argumentCaptor.capture());

        assertNotNull(trainee1);
        assertEquals(trainee1.getAddress(),"Bishkek");
        assertEquals(trainee1.getLocalDate(), LocalDate.now());
        assertEquals("Akhmatbek", argumentCaptor.getValue().getFirstName());
        assertEquals("Tursunbaev", argumentCaptor.getValue().getLastName());


    }

    @Test
    void updateTrainee() {
        User updated = new User(1L,"Tilek", "Toktobaev", "password", "Tilek.Toktobaev",true);
        TraineeDto traineeDto1 = new TraineeDto(updated.getFirstName(), updated.getLastName(), "Osh", LocalDate.of(2025,12,2));

        ArgumentCaptor<TraineeDto> argumentCaptor = ArgumentCaptor.forClass(TraineeDto.class);
        when(traineeDao.updateTrainee(any(), any())).thenAnswer(i-> i.getArgument(1));
        when(userService.updateUsersName(any(), any(UserDto.class))).thenReturn(updated);
        when(traineeDao.getTrainee(any())).thenReturn(new Trainee());

        Trainee trainee1 =  traineeService.updateTrainee(user.getUsername(),traineeDto1);

        verify(userService).updateUsersName(any(), argumentCaptor.capture());

        assertNotNull(trainee1);
        assertEquals(trainee1.getAddress(),traineeDto1.getAddress());
        assertEquals(trainee1.getLocalDate(), traineeDto1.getDateOfBirth());
        assertEquals(traineeDto1.getFirstName(), argumentCaptor.getValue().getFirstName());
        assertEquals(traineeDto1.getLastName(), argumentCaptor.getValue().getLastName());
    }


    @Test
    void deleteTrainee() {
        when(userService.findUserByUsername(user.getUsername())).thenReturn(user);

        traineeService.deleteTrainee(user.getUsername());

        verify(userService, times(1)).findUserByUsername(user.getUsername());
        verify(traineeDao, times(1)).deleteTrainee(any());
        verify(userService, times(1)).deleteUser(user.getUsername());

    }


}
