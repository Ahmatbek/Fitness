package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.biamino.projects.auth.AuthHandler;
import kg.biamino.projects.controllers.AuthController;
import kg.biamino.projects.dto.ChangePasswordDto;
import kg.biamino.projects.exception.AuthenticationException;
import kg.biamino.projects.exception.AuthorizationException;
import kg.biamino.projects.exception.GlobalExceptionHandler;
import kg.biamino.projects.service.UserService;
import kg.biamino.projects.service.impl.ErrorResponseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthHandler authHandler;
    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = TestObjectMappers.create();

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(authHandler, userService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler(new ErrorResponseServiceImpl()))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void login_validCredentials_returns200() throws Exception {
        when(authHandler.handle(any())).thenReturn("Aidana.Toktosunova");

        mockMvc.perform(get("/auth").header("Authorization", "Basic QWlkYW5hLlRva3Rvc3Vub3ZhOnBhc3MxMjM="))
                .andExpect(status().isOk());
    }

    @Test
    void login_authenticationFails_returns401() throws Exception {
        when(authHandler.handle(any())).thenThrow(new AuthenticationException("username and password do not match"));

        mockMvc.perform(get("/auth").header("Authorization", "Basic invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePassword_validRequest_returns200AndDelegatesToService() throws Exception {
        ChangePasswordDto dto = new ChangePasswordDto("Aidana.Toktosunova", "OldPass1", "NewPass1");
        when(authHandler.handle(any())).thenReturn("Aidana.Toktosunova");

        mockMvc.perform(put("/auth")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userService).changePassword(eq(dto), eq("Aidana.Toktosunova"));
    }

    @Test
    void changePassword_blankUsername_returns400() throws Exception {
        ChangePasswordDto dto = new ChangePasswordDto("", "OldPass1", "NewPass1");

        mockMvc.perform(put("/auth")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void changePassword_weakNewPassword_returns400() throws Exception {
        ChangePasswordDto dto = new ChangePasswordDto("Aidana.Toktosunova", "OldPass1", "weak");

        mockMvc.perform(put("/auth")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_authorizationMismatch_returns403() throws Exception {
        ChangePasswordDto dto = new ChangePasswordDto("Aidana.Toktosunova", "OldPass1", "NewPass1");
        when(authHandler.handle(any())).thenThrow(new AuthorizationException("cant modify other users"));

        mockMvc.perform(put("/auth")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }
}
