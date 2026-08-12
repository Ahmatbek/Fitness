package config;

import kg.biamino.projects.config.TrainingTypeHealthIndicator;
import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.repository.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TrainingTypeHealthIndicatorTest {

    @Test
    void health_recordExists_returnsUp() {
        TrainingTypeRepository repository = mock(TrainingTypeRepository.class);
        when(repository.findAll()).thenReturn(List.of(new TrainingType("individual")));

        Health health = new TrainingTypeHealthIndicator(repository).health();

        assertEquals(Status.UP, health.getStatus());
    }

    @Test
    void health_noRecords_returnsDown() {
        TrainingTypeRepository repository = mock(TrainingTypeRepository.class);
        when(repository.findAll()).thenReturn(List.of());

        Health health = new TrainingTypeHealthIndicator(repository).health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}
