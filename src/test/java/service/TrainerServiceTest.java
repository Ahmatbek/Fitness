//package service;
//
//import kg.biamino.projects.dao.TrainerDao;
//import kg.biamino.projects.dto.TrainerDto;
//import kg.biamino.projects.dto.UserDto;
//import kg.biamino.projects.model.Trainer;
//import kg.biamino.projects.model.User;
//import kg.biamino.projects.service.UserService;
//import kg.biamino.projects.service.impl.TrainerServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TrainerServiceTest {
//    @InjectMocks
//    private TrainerServiceImpl trainerService;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private TrainerDao trainerDao;
//
//    private Map<String, Trainer> trainers;
//    private User user;
//    private TrainerDto trainerDto;
//    private Trainer trainer;
//
//    @BeforeEach
//    void setUp() {
//        user = new User();
//        user.setId(1L);
//        user.setUsername("Ivan.Nasty");
//        user.setPassword("1234");
//        user.setLastName("Ivan");
//        user.setFirstName("Nasty");
//
//        trainerService.setTrainerDao(trainerDao);
//        trainerService.setUserService(userService);
//        trainers = new HashMap<>();
//
//        trainer = new Trainer();
//        trainer.setSpecialization("BOXING");
//        trainer.setUserId(user.getId());
//        trainers.put(user.getUsername(), trainer);
//
//        trainerDto = new TrainerDto("Akhmatbek","Tursunbaev", "BOXING");
//
//
//
//    }
//
//
//
//    @Test
//    void getAllTrainers() {
//        when(trainerDao.getAllTrainers()).thenReturn(new ArrayList<>(trainers.values()));
//
//        List<Trainer> trainerDtos = trainerService.getAllTrainers();
//
//        assertNotNull(trainerDtos);
//        assertEquals(trainerDtos.size(), trainers.size());
//        verify(trainerDao, times(1)).getAllTrainers();
//
//    }
//    @Test
//    void selectById(){
//        when(trainerDao.getTrainer(any())).thenReturn(trainers.get("Ivan.Nasty"));
//
//        Trainer trainer = trainerService.getTrainer(user.getId());
//
//        assertNotNull(trainer);
//        assertEquals(trainer.getSpecialization(), "BOXING");
//        assertEquals(trainer.getUserId(), user.getId());
//        verify(trainerDao,times(1)).getTrainer(any());
//    }
//
//    @Test
//    void createTrainerTest(){
//        ArgumentCaptor<TrainerDto> argumentCaptor = ArgumentCaptor.forClass(TrainerDto.class);
//        when(trainerDao.createTrainer(any(), any(Trainer.class))).thenReturn(trainer);
//        when(userService.createUser(any(UserDto.class))).thenReturn(user);
//
//        Trainer trainer1 =  trainerService.createTrainer(trainerDto);
//
//        verify(userService).createUser(argumentCaptor.capture());
//
//        assertNotNull(trainer1);
//        assertEquals(trainer1.getSpecialization(),"BOXING");
//        assertEquals("Akhmatbek", argumentCaptor.getValue().getFirstName());
//        assertEquals("Tursunbaev", argumentCaptor.getValue().getLastName());
//
//
//    }
//
//    @Test
//    void updateTest(){
//        User updated = new User(1L,"Tilek", "Toktobaev", "Tilek.Toktobaev", "password",true);
//        TrainerDto trainerDto1 = new TrainerDto(updated.getFirstName(), updated.getLastName(),"MMA");
//
//        ArgumentCaptor<TrainerDto> argumentCaptor = ArgumentCaptor.forClass(TrainerDto.class);
//        when(trainerDao.updateTrainer(any(), any())).thenAnswer(i-> i.getArgument(1));
//        when(userService.updateUsersName(any(), any(UserDto.class))).thenReturn(updated);
//        when(trainerDao.getTrainer(any())).thenReturn(new Trainer());
//
//
//        Trainer trainee1 =  trainerService.updateTrainer(user.getUsername(),trainerDto1);
//
//        verify(userService).updateUsersName(any(), argumentCaptor.capture());
//
//        assertNotNull(trainee1);
//        assertEquals(trainee1.getSpecialization(),trainerDto1.getSpecialization());
//        assertEquals(trainerDto1.getFirstName(), argumentCaptor.getValue().getFirstName());
//        assertEquals(trainerDto1.getLastName(), argumentCaptor.getValue().getLastName());
//    }
//
//    @Test
//    void shouldThrowIllegalArgumentException(){
//        assertThrows(IllegalArgumentException.class, ()->trainerService.createTrainer(null));
//    }
//
//    @Test
//    void shouldThrowIllegalArgumentException_specialization(){
//        trainerDto.setSpecialization(null);
//        assertThrows(IllegalArgumentException.class, ()->trainerService.createTrainer(trainerDto));
//    }
//
//
//
//    @Test
//    void shouldThrowIllegalArgumentException_firstName(){
//        trainerDto.setFirstName(null);
//        assertThrows(IllegalArgumentException.class, ()->trainerService.createTrainer(trainerDto));
//    }
//
//
//    @Test
//    void shouldThrowIllegalArgumentException_LastName(){
//        trainerDto.setLastName(null);
//        assertThrows(IllegalArgumentException.class, ()->trainerService.createTrainer(trainerDto));
//    }
//
//    @Test
//    void shouldThrowIllegalArgumentException_onUpdateDtoNull(){
//        assertThrows(IllegalArgumentException.class, ()->trainerService.updateTrainer(user.getUsername(),null));
//    }
//    @Test
//    void shouldThrowIllegalArgumentException_onUpdateUserNameNull(){
//        assertThrows(IllegalArgumentException.class, ()->trainerService.updateTrainer(null,trainerDto));
//    }
//}
