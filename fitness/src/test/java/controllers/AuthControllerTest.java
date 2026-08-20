package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.biamino.projects.config.JwtLogoutTokens;
import kg.biamino.projects.utils.JwtUtil;
import kg.biamino.projects.controllers.AuthController;
import kg.biamino.projects.dto.ChangePasswordDto;
import kg.biamino.projects.dto.LoginRequestDto;
import kg.biamino.projects.exception.AuthorizationException;
import kg.biamino.projects.exception.GlobalExceptionHandler;
import kg.biamino.projects.service.AuthService;
import kg.biamino.projects.service.UserService;
import kg.biamino.projects.service.impl.ErrorResponseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;
    @Mock
    private UserService userService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private JwtLogoutTokens jwtLogoutTokens;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = TestObjectMappers.create();

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController( userService, authService,jwtLogoutTokens, jwtUtil);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler(new ErrorResponseServiceImpl()))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void login_validCredentials_returns200() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("Akhamtbek.Tursunbaev", "password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void login_authenticationFails_returns401() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("Akhamtbek.Tursunbaev", "password");
        doThrow(new BadCredentialsException("Bad Credentials"))
                .when(authService).login(loginRequestDto);
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void changePassword_validRequest_returns200AndDelegatesToService() throws Exception {
        ChangePasswordDto dto = new ChangePasswordDto("Aidana.Toktosunova", "OldPass1", "NewPass1");


        mockMvc.perform(put("/auth")
                        .contentType("application/json")
                        .principal(()-> "Aidana.Toktosunova")
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
        ChangePasswordDto dto = new ChangePasswordDto("Aidana.Toktosunova", "OldPass1", "");

        mockMvc.perform(put("/auth")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void changePassword_authorizationMismatch_returns403() throws Exception {
        ChangePasswordDto dto = new ChangePasswordDto("Aidana.Toktosunova", "OldPass1", "NewPass1");

        doThrow(new AuthorizationException("..."))
                .when(userService).changePassword(eq(dto), eq("Someone"));

        mockMvc.perform(put("/auth")
                        .contentType("application/json")
                        .principal(()-> "Someone")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        verify(userService).changePassword(eq(dto), eq("Someone"));
    }

    @Test
    void logout_success() throws Exception {
        when(jwtUtil.extractExpiration(any(String.class))).thenReturn(new Date(System.currentTimeMillis()+9000000));
        when(jwtUtil.extractIdToken(any(String.class))).thenReturn("unique_token_id");
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());

        verify(jwtLogoutTokens).invalidate(eq("unique_token_id"),any());
        verify(jwtLogoutTokens,times(1)).invalidate(any(),any());
    }

    @Test
    void throw_exception_ok() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk());

        verifyNoInteractions(jwtUtil);
        verifyNoInteractions(jwtLogoutTokens);
        verifyNoInteractions(authService);
    }

    @Test
    void throw_dont_start_with_bearer() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Basic some-token"))
                .andExpect(status().isOk());
        verifyNoInteractions(jwtUtil);
        verifyNoInteractions(jwtLogoutTokens);
        verifyNoInteractions(authService);
    }



    @Test
    void dont_find_claims() throws Exception {

        when(jwtUtil.extractIdToken(any())).thenThrow(new BadCredentialsException("Bad Credentials"));
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer some-token")
                )
                .andExpect(status().isUnauthorized());

        assertThrows(BadCredentialsException.class, ()-> jwtUtil.extractIdToken(any()));
        verifyNoInteractions(jwtLogoutTokens);

    }

    @Test
    void outdated_token() throws Exception {

        when(jwtUtil.extractExpiration(any())).thenThrow(new BadCredentialsException("Outdated token"));
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer some-token")
                )
                .andExpect(status().isUnauthorized());

        assertThrows(BadCredentialsException.class,
                () -> jwtUtil.extractExpiration(any()));
        verifyNoInteractions(jwtLogoutTokens);

    }

    @Test
    void throw_bad_request_when_token_doesnt_have_jtiId_or_expiration() throws Exception {
        when(jwtUtil.extractIdToken(any())).thenReturn(null);
        assertNull(jwtUtil.extractIdToken("some-token"));
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer some-token")
                )
                .andExpect(status().isBadRequest());

        verify(jwtUtil, times(2)).extractIdToken(any());
        verifyNoInteractions(jwtLogoutTokens);

    }





}
