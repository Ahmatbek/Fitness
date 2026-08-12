package mapper;

import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.mapper.impl.TrainingMapper;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.service.TraineeService;
import kg.biamino.projects.service.TrainerService;
import kg.biamino.projects.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingMapperTest {

    @Mock
    private TrainingTypeService trainingTypeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TraineeService traineeService;

    @InjectMocks
    private TrainingMapper trainingMapper;



    @Test
    void toEntity_mapsAllFieldsFromDto() {
        TrainingDto dto = new TrainingDto();
        dto.setTraineeUsername("Nurlan");
        dto.setTrainerUsername("Bekzat");
        dto.setTrainingName("session-1");
        dto.setTrainingType("individual");
        dto.setTrainingStart(LocalDate.of(2026, 8, 1));
        dto.setDuration(60);

        TrainingType type = new TrainingType("individual");
        Trainer trainer = new Trainer();
        Trainee trainee = new Trainee();

        when(trainingTypeService.findByName("individual")).thenReturn(type);
        when(trainerService.getTrainerByUsername("Bekzat")).thenReturn(trainer);
        when(traineeService.getTraineeByUsername("Nurlan")).thenReturn(trainee);

        Training training = trainingMapper.toEntity(dto);

        assertEquals("session-1", training.getTrainingName());
        assertEquals(type, training.getTrainingType());
        assertEquals(LocalDate.of(2026, 8, 1), training.getDate());
        assertEquals(trainer, training.getTrainer());
        assertEquals(trainee, training.getTrainee());
        assertEquals(60, training.getDuration());
    }

    @Test
    void toDto_currentlyReturnsNull() {
        assertNull(trainingMapper.toDto(new Training()));
    }
}
