import kg.biamino.projects.dao.TrainingDao;
import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class TrainingServiceTest {

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Mock
    private TrainingDao trainingDao;

    private Map<String, Training> trainings;
    private Training training;
    private TrainingDto trainingDto;


    @BeforeEach
    void setUp() {
        trainings = new HashMap<>();
        TrainingType trainingType = new TrainingType("individual");
        training = new Training(1L,2L,"session-123", trainingType, LocalDate.now(), 90);

        trainings.put(training.getTrainingName(), training);

        trainingDto= new TrainingDto(1L, 2L, "session-123", trainingType, LocalDate.now(), 90);

    }

    @Test
    void getById(){
        when(trainingDao.getTraining(any())).thenReturn(trainings.get(training.getTrainingName()));

        Training training1 = trainingService.getTrainingById(training.getTrainingName());

        assertNotNull(training1);
        assertEquals(training.getTrainingName(), training1.getTrainingName());
        verify(trainingDao, times(1)).getTraining(training.getTrainingName());
    }

    @Test
    void getAllTrainings(){
        when(trainingDao.getAllTrainings()).thenReturn(new ArrayList<>(trainings.values()));

        List<Training> trainingList = trainingService.getAllTrainings();

        assertNotNull(trainingList);
        assertEquals(trainingList.size(), trainings.size());
        verify(trainingDao, times(1)).getAllTrainings();
    }

    @Test
    void createTraining(){
        when(trainingDao.createTraining(any(), any())).thenAnswer(i -> i.getArgument(1));

        Training result = trainingService.createTraining(trainingDto);

        assertNotNull(result);
        assertEquals(trainingDto.getTrainingName(), result.getTrainingName());
        assertEquals(trainingDto.getTrainerId(), result.getTrainerId());
        verify(trainingDao, times(1)).createTraining(any(), any());
    }

    @Test
    void createTraining_futureDate_throwsException(){
        trainingDto.setTrainingStart(LocalDate.now().plusDays(1));
        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(trainingDto));
    }


}
