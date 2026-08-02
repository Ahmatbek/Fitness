//package controllers;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import kg.biamino.projects.auth.AuthHandler;
//import kg.biamino.projects.controllers.TrainingController;
//import kg.biamino.projects.dto.TrainingDto;
//import kg.biamino.projects.exception.AuthenticationException;
//import kg.biamino.projects.exception.GlobalExceptionHandler;
//import kg.biamino.projects.exception.UserNotFoundException;
//import kg.biamino.projects.model.Training;
//import kg.biamino.projects.service.TrainingService;
//import kg.biamino.projects.service.impl.ErrorResponseServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.time.LocalDate;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(MockitoExtension.class)
//class TrainingControllerTest {
//
//    @Mock
//    private TrainingService trainingService;
//    @Mock
//    private AuthHandler authHandler;
//
//    private MockMvc mockMvc;
//    private final ObjectMapper objectMapper = TestObjectMappers.create();
//
//    @BeforeEach
//    void setUp() {
//        TrainingController controller = new TrainingController(trainingService, authHandler);
//        mockMvc = MockMvcBuilders.standaloneSetup(controller)
//                .setControllerAdvice(new GlobalExceptionHandler(new ErrorResponseServiceImpl()))
//                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
//                .build();
//    }
//
//    private TrainingDto validDto() {
//        return new TrainingDto("Dilmurod.Sadyrov", "Aidana.Toktosunova", "Yoga", "individual", LocalDate.now(), 60);
//    }
//
//    @Test
//    void createTraining_validRequest_returns200AndDelegatesToService() throws Exception {
//        when(authHandler.handle(any())).thenReturn("Dilmurod.Sadyrov");
//        when(trainingService.createTraining(any())).thenReturn(new Training());
//
//        mockMvc.perform(post("/trainings")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(validDto())))
//                .andExpect(status().isOk());
//
//        verify(trainingService).createTraining(any());
//    }
//
//    @Test
//    void createTraining_blankTraineeUsername_returns400() throws Exception {
//        TrainingDto dto = new TrainingDto("", "Aidana.Toktosunova", "Yoga", "individual", LocalDate.now(), 60);
//
//        mockMvc.perform(post("/trainings")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest());
//
//        verifyNoInteractions(trainingService);
//    }
//
//    @Test
//    void createTraining_authenticationFails_returns401() throws Exception {
//        when(authHandler.handle(any())).thenThrow(new AuthenticationException("bad credentials"));
//
//        mockMvc.perform(post("/trainings")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(validDto())))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void createTraining_traineeNotFound_returns404() throws Exception {
//        when(authHandler.handle(any())).thenReturn("Dilmurod.Sadyrov");
//        when(trainingService.createTraining(any())).thenThrow(new UserNotFoundException("trainee not found"));
//
//        mockMvc.perform(post("/trainings")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(validDto())))
//                .andExpect(status().isNotFound());
//    }
//}
