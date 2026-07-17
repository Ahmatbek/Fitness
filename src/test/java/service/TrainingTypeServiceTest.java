package service;

import kg.biamino.projects.dto.TrainingTypeDto;
import kg.biamino.projects.exception.TrainingTypeNotFoundException;
import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.repository.TrainingTypeRepository;
import kg.biamino.projects.service.impl.TrainingTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
    void findById_notFound_throwsTrainingTypeNotFoundException() {
        when(trainingTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TrainingTypeNotFoundException.class, () -> trainingTypeService.findById(99L));
    }

    @Test
    void findByName_found_returnsTrainingType() {
        TrainingType type = new TrainingType("group");
        when(trainingTypeRepository.findByName("group")).thenReturn(Optional.of(type));

        assertEquals(type, trainingTypeService.findByName("group"));
    }

    @Test
    void findByName_notFound_throwsTrainingTypeNotFoundException() {
        when(trainingTypeRepository.findByName("unknown")).thenReturn(Optional.empty());

        assertThrows(TrainingTypeNotFoundException.class, () -> trainingTypeService.findByName("unknown"));
    }

    @Test
    void findAll_mapsEntitiesToDtos() {
        TrainingType individual = new TrainingType("individual");
        individual.setId(1L);
        TrainingType group = new TrainingType("group");
        group.setId(2L);
        when(trainingTypeRepository.findAll()).thenReturn(List.of(individual, group));

        List<TrainingTypeDto> result = trainingTypeService.findAll();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("individual", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("group", result.get(1).getName());
    }
}
