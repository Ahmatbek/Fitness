package service;

import kg.biamino.projects.dto.AuthUserDto;
import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.mapper.impl.TrainingMapper;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.repository.TrainingRepository;
import kg.biamino.projects.service.UserService;
import kg.biamino.projects.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private UserService userService;
    @Mock
    private TrainingMapper trainingMapper;

    private TrainingServiceImpl trainingService;

    private final AuthUserDto authUserDto = new AuthUserDto("Nurlan.Bekov", "pass123");

    @BeforeEach
    void setUp() {
        trainingService = new TrainingServiceImpl(userService, trainingMapper);
        trainingService.setTrainingRepository(trainingRepository);
    }

    private TrainingDto validDto() {
        TrainingDto dto = new TrainingDto();
        dto.setTraineeId(1L);
        dto.setTrainerId(2L);
        dto.setTrainingName("session-1");
        dto.setTrainingType("individual");
        dto.setTrainingStart(LocalDate.now().plusDays(1));
        dto.setDuration(60);
        return dto;
    }

    @Test
    void getTrainingById_found_returnsTraining() {
        Training training = new Training();
        when(trainingRepository.findById(5L)).thenReturn(Optional.of(training));

        assertEquals(training, trainingService.getTrainingById(5L));
    }

    @Test
    void getTrainingById_notFound_throwsNoSuchElementException() {
        when(trainingRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainingService.getTrainingById(5L));
    }

    @Test
    void getAllTrainings_returnsRepositoryResult() {
        List<Training> trainings = List.of(new Training());
        when(trainingRepository.findAll()).thenReturn(trainings);

        assertEquals(trainings, trainingService.getAllTrainings());
    }

    @Test
    void createTraining_success_savesMappedEntity() {
        TrainingDto dto = validDto();
        Training mapped = new Training();
        when(trainingMapper.toEntity(dto)).thenReturn(mapped);
        when(trainingRepository.save(mapped)).thenReturn(mapped);

        Training result = trainingService.createTraining(authUserDto, dto);

        assertEquals(mapped, result);
        verify(userService).userAuthenticated(authUserDto.getUsername(), authUserDto.getPassword());
    }

    @Test
    void createTraining_nullDto_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(authUserDto, null));
        verifyNoInteractions(trainingMapper);
    }

    @Test
    void createTraining_blankName_throwsIllegalArgumentException() {
        TrainingDto dto = validDto();
        dto.setTrainingName("   ");

        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(authUserDto, dto));
    }

    @Test
    void createTraining_pastStartDate_throwsIllegalArgumentException() {
        TrainingDto dto = validDto();
        dto.setTrainingStart(LocalDate.now().minusDays(1));

        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(authUserDto, dto));
    }

    @Test
    void createTraining_trainerIdZero_throwsIllegalArgumentException() {
        TrainingDto dto = validDto();
        dto.setTrainerId(0L);

        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(authUserDto, dto));
    }

    @Test
    void createTraining_traineeIdNegative_throwsIllegalArgumentException() {
        TrainingDto dto = validDto();
        dto.setTraineeId(-1L);

        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(authUserDto, dto));
    }

    @Test
    void createTraining_durationZero_throwsIllegalArgumentException() {
        TrainingDto dto = validDto();
        dto.setDuration(0);

        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(authUserDto, dto));
    }

    @Test
    void createTraining_nullTrainingType_throwsIllegalArgumentException() {
        TrainingDto dto = validDto();
        dto.setTrainingType(null);

        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(authUserDto, dto));
    }

    @Test
    void createTraining_nullTrainingStart_throwsIllegalArgumentException() {
        TrainingDto dto = validDto();
        dto.setTrainingStart(null);

        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(authUserDto, dto));
    }
}
