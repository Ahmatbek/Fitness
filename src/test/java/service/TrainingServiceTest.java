package service;

import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.mapper.impl.TrainingMapper;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TrainingMapper trainingMapper;

    private TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        trainingService = new TrainingServiceImpl(trainingMapper);
        trainingService.setTrainingRepository(trainingRepository);
    }

    private TrainingDto validDto() {
        return new TrainingDto("Aida.Sadykova", "Bekzat.Isakov", "session-1",
                "individual", LocalDate.now().plusDays(1), 60);
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
    void createTraining_success_savesMappedEntity() {
        TrainingDto dto = validDto();
        Training mapped = new Training();
        when(trainingMapper.toEntity(dto)).thenReturn(mapped);
        when(trainingRepository.save(mapped)).thenReturn(mapped);

        Training result = trainingService.createTraining(dto);

        assertEquals(mapped, result);
        verify(trainingRepository).save(mapped);
    }

    @Test
    void createTraining_nullDto_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(null));
        verifyNoInteractions(trainingMapper, trainingRepository);
    }

    @Test
    void createTraining_nullTrainingStart_throwsIllegalArgumentException() {
        TrainingDto dto = validDto();
        dto.setTrainingStart(null);

        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(dto));
        verifyNoInteractions(trainingMapper, trainingRepository);
    }

    @Test
    void createTraining_blankName_throwsIllegalArgumentException() {
        TrainingDto dto = validDto();
        dto.setTrainingName("   ");

        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(dto));
        verifyNoInteractions(trainingMapper, trainingRepository);
    }

    @Test
    void createTraining_pastStartDate_throwsIllegalArgumentException() {
        TrainingDto dto = validDto();
        dto.setTrainingStart(LocalDate.now().minusDays(1));

        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(dto));
        verifyNoInteractions(trainingMapper, trainingRepository);
    }

    @Test
    void createTraining_durationZero_throwsIllegalArgumentException() {
        TrainingDto dto = validDto();
        dto.setDuration(0);

        assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(dto));
        verifyNoInteractions(trainingMapper, trainingRepository);
    }
}
