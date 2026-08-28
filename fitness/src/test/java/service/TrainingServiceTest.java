package service;

import kg.biamino.projects.enums.ActionType;
import kg.biamino.projects.dto.TrainerWorkloadRequest;
import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.mapper.impl.TrainingMapper;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.User;
import kg.biamino.projects.repository.TrainingRepository;
import kg.biamino.projects.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

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
    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private TrainingServiceImpl trainingService;


    private TrainingDto validDto() {
        return new TrainingDto("Aida.Sadykova", "Bekzat.Isakov", "session-1",
                "individual", LocalDate.now().plusDays(1), 60);
    }

    private Training mappedTrainingWithTrainer() {
        User user = new User();
        user.setUsername("Bekzat.Isakov");
        user.setFirstName("Bekzat");
        user.setLastName("Isakov");
        user.setIsActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        Training training = new Training();
        training.setTrainer(trainer);
        training.setDate(LocalDate.now().plusDays(1));
        training.setDuration(60);
        return training;
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
        Training mapped = mappedTrainingWithTrainer();
        when(trainingMapper.toEntity(dto)).thenReturn(mapped);
        when(trainingRepository.save(mapped)).thenReturn(mapped);

        Training result = trainingService.createTraining(dto);

        assertEquals(mapped, result);
        verify(trainingRepository).save(mapped);

        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(jmsTemplate).convertAndSend(any(String.class), captor.capture());
        TrainerWorkloadRequest request = captor.getValue();
        assertEquals("Bekzat.Isakov", request.getTrainerUsername());
        assertEquals(ActionType.ADD, request.getActionType());
        assertEquals(mapped.getDate(), request.getTrainingDate());
        assertEquals(mapped.getDuration(), request.getTrainingDuration());

    }


    @Test
    void deleteTrainingById_existing_deletesAndNotifiesWorkloadService() {
        Training training = mappedTrainingWithTrainer();
        when(trainingRepository.findById(9L)).thenReturn(Optional.of(training));

        trainingService.deleteTrainingById(9L);

        verify(trainingRepository).delete(training);

        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(jmsTemplate).convertAndSend(any(String.class), captor.capture());
        assertEquals(ActionType.DELETE, captor.getValue().getActionType());
        assertEquals("Bekzat.Isakov", captor.getValue().getTrainerUsername());
    }

    @Test
    void deleteTrainingById_notFound_throwsNoSuchElementException() {
        when(trainingRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainingService.deleteTrainingById(9L));

        verify(trainingRepository, never()).delete(any(Training.class));
    }


}
