package config;

import kg.biamino.projects.config.UserHealthIndicator;
import kg.biamino.projects.model.User;
import kg.biamino.projects.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserHealthIndicatorTest {

    @Test
    void health_userExists_returnsUp() {
        UserRepository repository = mock(UserRepository.class);
        when(repository.findAll()).thenReturn(List.of(new User()));

        Health health = new UserHealthIndicator(repository).health();

        assertEquals(Status.UP, health.getStatus());
    }

    @Test
    void health_noUsers_returnsDown() {
        UserRepository repository = mock(UserRepository.class);
        when(repository.findAll()).thenReturn(List.of());

        Health health = new UserHealthIndicator(repository).health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}
