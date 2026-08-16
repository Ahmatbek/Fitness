package service;

import kg.biamino.projects.config.BruteForceProtectionService;
import kg.biamino.projects.utils.JwtUtil;
import kg.biamino.projects.dto.LoginRequestDto;
import kg.biamino.projects.dto.TokenResponseDto;
import kg.biamino.projects.exception.UserInactiveException;
import kg.biamino.projects.model.User;
import kg.biamino.projects.repository.UserRepository;
import kg.biamino.projects.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private BruteForceProtectionService bruteForceProtectionService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User activeUser;

    @BeforeEach
    void setUp() {
        activeUser = new User(1L, "Aidana", "Toktosunova", "Aidana.Toktosunova", "hashed-password", true);
    }

    @Test
    void login_validCredentials_returnsToken() {
        LoginRequestDto dto = new LoginRequestDto("Aidana.Toktosunova", "password123");
        when(userRepository.findUserByUsername("Aidana.Toktosunova")).thenReturn(Optional.of(activeUser));
        when(bruteForceProtectionService.isBlocked("Aidana.Toktosunova")).thenReturn(false);
        when(passwordEncoder.matches("password123", "hashed-password")).thenReturn(true);
        when(jwtUtil.generateToken("Aidana.Toktosunova")).thenReturn("jwt-token");

        TokenResponseDto result = authService.login(dto);

        assertEquals("jwt-token", result.token());
        verify(bruteForceProtectionService).loginSucceeded("Aidana.Toktosunova");
        verify(bruteForceProtectionService, never()).loginFailed(any());
    }

    @Test
    void login_unknownUsername_throwsBadCredentials() {
        LoginRequestDto dto = new LoginRequestDto("Nobody", "password123");
        when(userRepository.findUserByUsername("Nobody")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(dto));
        verifyNoInteractions(bruteForceProtectionService, jwtUtil);
    }

    @Test
    void login_userBlocked_throwsBadCredentialsWithoutCheckingPassword() {
        LoginRequestDto dto = new LoginRequestDto("Aidana.Toktosunova", "password123");
        when(userRepository.findUserByUsername("Aidana.Toktosunova")).thenReturn(Optional.of(activeUser));
        when(bruteForceProtectionService.isBlocked("Aidana.Toktosunova")).thenReturn(true);

        assertThrows(BadCredentialsException.class, () -> authService.login(dto));
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_inactiveUser_throwsUserInactiveException() {
        User inactiveUser = new User(1L, "Aidana", "Toktosunova", "Aidana.Toktosunova", "hashed-password", false);
        LoginRequestDto dto = new LoginRequestDto("Aidana.Toktosunova", "password123");
        when(userRepository.findUserByUsername("Aidana.Toktosunova")).thenReturn(Optional.of(inactiveUser));
        when(bruteForceProtectionService.isBlocked("Aidana.Toktosunova")).thenReturn(false);

        assertThrows(UserInactiveException.class, () -> authService.login(dto));
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_wrongPassword_recordsFailureAndThrowsBadCredentials() {
        LoginRequestDto dto = new LoginRequestDto("Aidana.Toktosunova", "wrong-password");
        when(userRepository.findUserByUsername("Aidana.Toktosunova")).thenReturn(Optional.of(activeUser));
        when(bruteForceProtectionService.isBlocked("Aidana.Toktosunova")).thenReturn(false);
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(dto));

        verify(bruteForceProtectionService).loginFailed("Aidana.Toktosunova");
        verify(bruteForceProtectionService, never()).loginSucceeded(any());
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void login_repeatedWrongPassword_recordsEachFailureWithBruteForceProtection() {
        LoginRequestDto dto = new LoginRequestDto("Aidana.Toktosunova", "wrong-password");
        when(userRepository.findUserByUsername("Aidana.Toktosunova")).thenReturn(Optional.of(activeUser));
        when(bruteForceProtectionService.isBlocked("Aidana.Toktosunova")).thenReturn(false);
        when(passwordEncoder.matches(eq("wrong-password"), any())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(dto));
        assertThrows(BadCredentialsException.class, () -> authService.login(dto));
        assertThrows(BadCredentialsException.class, () -> authService.login(dto));

        verify(bruteForceProtectionService, times(3)).loginFailed("Aidana.Toktosunova");
    }
}
