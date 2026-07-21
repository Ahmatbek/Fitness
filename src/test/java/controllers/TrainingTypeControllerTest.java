package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.biamino.projects.auth.AuthHandler;
import kg.biamino.projects.controllers.TrainingTypeController;
import kg.biamino.projects.dto.TrainingTypeDto;
import kg.biamino.projects.exception.AuthenticationException;
import kg.biamino.projects.exception.GlobalExceptionHandler;
import kg.biamino.projects.service.TrainingTypeService;
import kg.biamino.projects.service.impl.ErrorResponseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TrainingTypeControllerTest {

    @Mock
    private TrainingTypeService trainingTypeService;
    @Mock
    private AuthHandler authHandler;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = TestObjectMappers.create();

    @BeforeEach
    void setUp() {
        TrainingTypeController controller = new TrainingTypeController(trainingTypeService, authHandler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler(new ErrorResponseServiceImpl()))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void getAllTrainingTypes_authenticated_returns200WithList() throws Exception {
        when(trainingTypeService.findAll()).thenReturn(List.of(
                TrainingTypeDto.builder().id(1).name("individual").build(),
                TrainingTypeDto.builder().id(2).name("group").build()
        ));

        mockMvc.perform(get("/training-types").header("Authorization", "Basic dummy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("individual"));
    }

    @Test
    void getAllTrainingTypes_authenticationFails_returns401() throws Exception {
        when(authHandler.handle(any())).thenThrow(new AuthenticationException("bad credentials"));

        mockMvc.perform(get("/training-types"))
                .andExpect(status().isUnauthorized());
    }
}
