package service;

import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.repository.TrainingTypeRepository;
import kg.biamino.projects.service.impl.TrainingTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    private TrainingTypeServiceImpl trainingTypeService;

    @BeforeEach
    void setUp() {
        trainingTypeService = new TrainingTypeServiceImpl();
        trainingTypeService.setTrainingTypeRepository(trainingTypeRepository);
    }

    @Test
    void findById_found_returnsTrainingType() {
        TrainingType type = new TrainingType("individual");
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(type));

        assertEquals(type, trainingTypeService.findById(1L));
    }

    @Test
    void findById_notFound_throwsNoSuchElementException() {
        when(trainingTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainingTypeService.findById(99L));
    }

    @Test
    void findByName_found_returnsTrainingType() {
        TrainingType type = new TrainingType("group");
        when(trainingTypeRepository.findByName("group")).thenReturn(Optional.of(type));

        assertEquals(type, trainingTypeService.findByName("group"));
    }

    @Test
    void findByName_notFound_throwsNoSuchElementException() {
        when(trainingTypeRepository.findByName("unknown")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainingTypeService.findByName("unknown"));
    }
}
