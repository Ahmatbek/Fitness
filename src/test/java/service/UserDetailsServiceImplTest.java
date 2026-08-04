package service;

import kg.biamino.projects.exception.UserInactiveException;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.model.User;
import kg.biamino.projects.repository.TraineeRepository;
import kg.biamino.projects.repository.TrainerRepository;
import kg.biamino.projects.repository.UserRepository;
import kg.biamino.projects.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;



    private User activeUser() {
        return new User(1L, "Dilmurod", "Sadyrov", "Dilmurod.Sadyrov", "hashed-password", true);
    }

    @Test
    void loadUserByUsername_trainee_resolvesTraineeAuthority() {
        User user = activeUser();
        when(userRepository.findUserByUsername("Dilmurod.Sadyrov")).thenReturn(Optional.of(user));
        when(traineeRepository.findTraineeByUserId(1L)).thenReturn(Optional.of(mock(Trainee.class)));

        UserDetails userDetails = userDetailsService.loadUserByUsername("Dilmurod.Sadyrov");

        assertEquals("Dilmurod.Sadyrov", userDetails.getUsername());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("TRAINEE", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_trainer_resolvesTrainerAuthority() {
        User user = activeUser();
        when(userRepository.findUserByUsername("Dilmurod.Sadyrov")).thenReturn(Optional.of(user));
        when(traineeRepository.findTraineeByUserId(1L)).thenReturn(Optional.empty());
        when(trainerRepository.findByUserId(1L)).thenReturn(Optional.of(mock(Trainer.class)));

        UserDetails userDetails = userDetailsService.loadUserByUsername("Dilmurod.Sadyrov");

        assertEquals("TRAINER", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_inactiveUser_throwsUserInactiveException() {
        User inactiveUser = new User(1L, "Dilmurod", "Sadyrov", "Dilmurod.Sadyrov", "hashed-password", false);
        when(userRepository.findUserByUsername("Dilmurod.Sadyrov")).thenReturn(Optional.of(inactiveUser));

        assertThrows(UserInactiveException.class, () -> userDetailsService.loadUserByUsername("Dilmurod.Sadyrov"));
    }

    @Test
    void loadUserByUsername_unknownUsername_throwsUsernameNotFound() {
        when(userRepository.findUserByUsername("nobody")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("nobody"));
    }
}
