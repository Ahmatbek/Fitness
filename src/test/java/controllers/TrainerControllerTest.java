//package controllers;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import kg.biamino.projects.auth.AuthHandler;
//import kg.biamino.projects.controllers.TrainerController;
//import kg.biamino.projects.dto.ChangeStatusDto;
//import kg.biamino.projects.dto.TrainerDto;
//import kg.biamino.projects.dto.TrainerTraineesListDto;
//import kg.biamino.projects.dto.TrainerTrainingsDto;
//import kg.biamino.projects.dto.UpdateTrainerDto;
//import kg.biamino.projects.dto.UserCredentialsDto;
//import kg.biamino.projects.exception.AuthenticationException;
//import kg.biamino.projects.exception.GlobalExceptionHandler;
//import kg.biamino.projects.service.TrainerService;
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
//import java.util.List;
//import java.util.NoSuchElementException;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//class TrainerControllerTest {
//
//    @Mock
//    private TrainerService trainerService;
//    @Mock
//    private AuthHandler authHandler;
//
//    private MockMvc mockMvc;
//    private final ObjectMapper objectMapper = TestObjectMappers.create();
//
//    @BeforeEach
//    void setUp() {
//        TrainerController controller = new TrainerController(trainerService, authHandler);
//        mockMvc = MockMvcBuilders.standaloneSetup(controller)
//                .setControllerAdvice(new GlobalExceptionHandler(new ErrorResponseServiceImpl()))
//                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
//                .build();
//    }
//
//    @Test
//    void addTrainer_validRequest_returns200WithCredentials() throws Exception {
//        TrainerDto dto = TrainerDto.builder().firstName("Aidana").lastName("Toktosunova").specialization("individual").build();
//        UserCredentialsDto credentials = UserCredentialsDto.builder().username("Aidana.Toktosunova").password("pass123").build();
//        when(trainerService.createTrainer(any())).thenReturn(credentials);
//
//        mockMvc.perform(post("/trainers")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.username").value("Aidana.Toktosunova"));
//    }
//
//    @Test
//    void addTrainer_blankSpecialization_returns400() throws Exception {
//        TrainerDto dto = TrainerDto.builder().firstName("Aidana").lastName("Toktosunova").specialization("").build();
//
//        mockMvc.perform(post("/trainers")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest());
//
//        verifyNoInteractions(trainerService);
//    }
//
//    @Test
//    void findByUsername_authenticated_returns200() throws Exception {
//        when(trainerService.findByUsername("Aidana.Toktosunova"))
//                .thenReturn(TrainerTraineesListDto.builder().firstName("Aidana").lastName("Toktosunova").specialization("individual").isActive(true).trainees(List.of()).build());
//
//        mockMvc.perform(get("/trainers").param("username", "Aidana.Toktosunova")
//                        .header("Authorization", "Basic dummy"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.specialization").value("individual"));
//    }
//
//    @Test
//    void findByUsername_authenticationFails_returns401() throws Exception {
//        when(authHandler.handle(any())).thenThrow(new AuthenticationException("bad credentials"));
//
//        mockMvc.perform(get("/trainers").param("username", "Aidana.Toktosunova"))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void findByUsername_notFound_returns404() throws Exception {
//        when(trainerService.findByUsername("nonexistent")).thenThrow(new NoSuchElementException("trainer not found"));
//
//        mockMvc.perform(get("/trainers").param("username", "nonexistent"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void updateTrainer_validRequest_returns200() throws Exception {
//        UpdateTrainerDto dto = UpdateTrainerDto.builder().firstName("Aidana").lastName("Toktosunova").username("Aidana.Toktosunova").isActive(true).build();
//        when(authHandler.handle(any())).thenReturn("Aidana.Toktosunova");
//        when(trainerService.updateTrainer(any(), eq("Aidana.Toktosunova")))
//                .thenReturn(TrainerTraineesListDto.builder().firstName("Aidana").lastName("Toktosunova").trainees(List.of()).build());
//
//        mockMvc.perform(put("/trainers")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void updateTrainer_missingIsActive_returns400() throws Exception {
//        UpdateTrainerDto dto = UpdateTrainerDto.builder().firstName("Aidana").lastName("Toktosunova").username("Aidana.Toktosunova").build();
//
//        mockMvc.perform(put("/trainers")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void changeStatus_validRequest_returns200() throws Exception {
//        ChangeStatusDto dto = new ChangeStatusDto();
//        dto.setUsername("Aidana.Toktosunova");
//        dto.setIsActive(false);
//        when(authHandler.handle(any())).thenReturn("Aidana.Toktosunova");
//
//        mockMvc.perform(patch("/trainers")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk());
//
//        verify(trainerService).changeStatusTrainer(any(), eq("Aidana.Toktosunova"));
//    }
//
//    @Test
//    void changeStatus_missingStatus_returns400() throws Exception {
//        ChangeStatusDto dto = new ChangeStatusDto();
//        dto.setUsername("Aidana.Toktosunova");
//
//        mockMvc.perform(patch("/trainers")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void getTraineeTrainings_validRequest_returns200() throws Exception {
//        TrainerTrainingsDto dto = TrainerTrainingsDto.builder().username("Aidana.Toktosunova").build();
//        when(authHandler.handle(any())).thenReturn("Aidana.Toktosunova");
//        when(trainerService.getTrainingsByCriteria(any())).thenReturn(List.of());
//
//        mockMvc.perform(get("/trainers/trainings")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void getTraineeTrainings_blankUsername_returns400() throws Exception {
//        TrainerTrainingsDto dto = TrainerTrainingsDto.builder().username("").build();
//
//        mockMvc.perform(get("/trainers/trainings")
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isBadRequest());
//    }
//}
