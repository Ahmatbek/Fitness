package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.biamino.projects.auth.AuthHandler;
import kg.biamino.projects.controllers.TraineeController;
import kg.biamino.projects.dto.TraineeDto;
import kg.biamino.projects.dto.UserCredentialsDto;
import kg.biamino.projects.exception.GlobalExceptionHandler;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.model.User;
import kg.biamino.projects.service.TraineeService;
import kg.biamino.projects.service.impl.ErrorResponseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ExampleController {

    @Mock
    private AuthHandler authHandler;

    @Mock
    private TraineeService traineeService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = TestObjectMappers.create();

    @BeforeEach
    public void setup() {
        TraineeController controller = new TraineeController(traineeService, authHandler);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler(new ErrorResponseServiceImpl()))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void createTrainee() throws Exception {
        TraineeDto dto = TraineeDto.builder().firstName("Dilmurod").lastName("Sadyrov").build();
        UserCredentialsDto credentialsDto = UserCredentialsDto.builder().username("Akhmat.Tursunbaev").password("pass").build();
        when(traineeService.createTrainee(any())).thenReturn(credentialsDto);


        mockMvc.perform(post("/trainees")
                .content(objectMapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Akhmat.Tursunbaev"));

    }
}
