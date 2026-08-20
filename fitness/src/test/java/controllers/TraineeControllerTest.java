package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.biamino.projects.controllers.TraineeController;
import kg.biamino.projects.dto.ChangeStatusDto;
import kg.biamino.projects.dto.TraineeDto;
import kg.biamino.projects.dto.TraineeTrainersListDto;
import kg.biamino.projects.dto.TraineeTrainingsDto;
import kg.biamino.projects.dto.TrainerUsernameDto;
import kg.biamino.projects.dto.UpdateTraineeDto;
import kg.biamino.projects.dto.UpdateTraineeTrainersDto;
import kg.biamino.projects.dto.UserCredentialsDto;
import kg.biamino.projects.exception.AuthorizationException;
import kg.biamino.projects.exception.DateInvalidException;
import kg.biamino.projects.exception.GlobalExceptionHandler;
import kg.biamino.projects.service.TraineeService;
import kg.biamino.projects.service.impl.ErrorResponseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    @Mock
    private TraineeService traineeService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = TestObjectMappers.create();

    @BeforeEach
    void setUp() {
        TraineeController controller = new TraineeController(traineeService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler(new ErrorResponseServiceImpl()))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void registerTrainee_validRequest_returns200WithCredentials() throws Exception {
        TraineeDto dto = TraineeDto.builder().firstName("Dilmurod").lastName("Sadyrov")
                .address("bishkek").dateOfBirth(LocalDate.of(2024, 2, 2)).build();
        UserCredentialsDto credentials = UserCredentialsDto.builder().username("Dilmurod.Sadyrov").password("pass123").build();
        when(traineeService.createTrainee(any())).thenReturn(credentials);

        mockMvc.perform(post("/trainees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Dilmurod.Sadyrov"));

        verify(traineeService).createTrainee(any());
    }

    @Test
    void registerTrainee_blankFirstName_returns400() throws Exception {
        TraineeDto dto = TraineeDto.builder().firstName("").lastName("Sadyrov").build();

        mockMvc.perform(post("/trainees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(traineeService);
    }

    @Test
    void registerTrainee_dateOfBirthInFuture_returns400() throws Exception {
        TraineeDto dto = TraineeDto.builder().firstName("John").lastName("Doe")
                .dateOfBirth(LocalDate.of(2027, 2, 2)).build();
        doThrow(new DateInvalidException("date is in future")).when(traineeService).createTrainee(any());

        mockMvc.perform(post("/trainees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(traineeService).createTrainee(any());
    }

    @Test
    void getTraineeByUsername_returns200() throws Exception {
        TraineeTrainersListDto result = TraineeTrainersListDto.builder().firstName("Dilmurod").lastName("Sadyrov").isActive(true).trainers(List.of()).build();
        when(traineeService.findByUsername("Dilmurod.Sadyrov")).thenReturn(result);

        mockMvc.perform(get("/trainees").param("username", "Dilmurod.Sadyrov"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Dilmurod"));
    }

    @Test
    void getTraineeByUsername_notFound_returns404() throws Exception {
        when(traineeService.findByUsername("nonexistent")).thenThrow(new NoSuchElementException("trainee not found"));

        mockMvc.perform(get("/trainees").param("username", "nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTraineeByUsername_validRequest_returns200() throws Exception {
        UpdateTraineeDto dto = UpdateTraineeDto.builder().firstName("Dilmurod").lastName("Sadyrov").username("Dilmurod.Sadyrov").isActive(true).build();
        when(traineeService.updateTrainee(any(), eq("Dilmurod.Sadyrov")))
                .thenReturn(TraineeTrainersListDto.builder().firstName("Dilmurod").lastName("Sadyrov").trainers(List.of()).build());

        mockMvc.perform(put("/trainees")
                        .contentType("application/json")
                        .principal(() -> "Dilmurod.Sadyrov")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(traineeService).updateTrainee(any(), eq("Dilmurod.Sadyrov"));
    }

    @Test
    void updateTraineeByUsername_missingIsActive_returns400() throws Exception {
        UpdateTraineeDto dto = UpdateTraineeDto.builder().firstName("Dilmurod").lastName("Sadyrov").username("Dilmurod.Sadyrov").build();

        mockMvc.perform(put("/trainees")
                        .contentType("application/json")
                        .principal(() -> "Dilmurod.Sadyrov")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTraineeByUsername_success_returns200() throws Exception {
        mockMvc.perform(delete("/trainees").param("username", "Dilmurod.Sadyrov")
                        .principal(() -> "Dilmurod.Sadyrov"))
                .andExpect(status().isOk());

        verify(traineeService).deleteTraineeByUsername("Dilmurod.Sadyrov", "Dilmurod.Sadyrov");
    }

    @Test
    void deleteTraineeByUsername_notOwnProfile_returns403() throws Exception {
        doThrow(new AuthorizationException("cant delete other users")).when(traineeService)
                .deleteTraineeByUsername("Dilmurod.Sadyrov", "SomeoneElse");

        mockMvc.perform(delete("/trainees").param("username", "Dilmurod.Sadyrov")
                        .principal(() -> "SomeoneElse"))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeStatus_validRequest_returns200() throws Exception {
        ChangeStatusDto dto = new ChangeStatusDto();
        dto.setUsername("Dilmurod.Sadyrov");
        dto.setIsActive(false);

        mockMvc.perform(patch("/trainees")
                        .contentType("application/json")
                        .principal(() -> "Dilmurod.Sadyrov")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(traineeService).changeStatusTrainer(any(), eq("Dilmurod.Sadyrov"));
    }

    @Test
    void changeStatus_blankUsername_returns400() throws Exception {
        ChangeStatusDto dto = new ChangeStatusDto();
        dto.setUsername("");
        dto.setIsActive(false);

        mockMvc.perform(patch("/trainees")
                        .contentType("application/json")
                        .principal(() -> "Dilmurod.Sadyrov")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTrainersNotAssignedByTraineeByUsername_returns200WithList() throws Exception {
        when(traineeService.findNotAssignedTrainersByUsername("Dilmurod.Sadyrov"))
                .thenReturn(List.of(TrainerUsernameDto.builder().firstName("Aidana").lastName("Toktosunova").username("Aidana.Toktosunova").specialization("individual").build()));

        mockMvc.perform(get("/trainees/not-assigned").param("username", "Dilmurod.Sadyrov"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("Aidana.Toktosunova"));
    }

    @Test
    void updateTraineeTrainers_validRequest_returns200() throws Exception {
        UpdateTraineeTrainersDto dto = UpdateTraineeTrainersDto.builder().username("Dilmurod.Sadyrov").trainers(List.of("Aidana.Toktosunova")).build();
        when(traineeService.updateTrainersByUsername(any(), eq("Dilmurod.Sadyrov"))).thenReturn(List.of());

        mockMvc.perform(put("/trainees/update-trainers")
                        .contentType("application/json")
                        .principal(() -> "Dilmurod.Sadyrov")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateTraineeTrainers_blankUsername_returns400() throws Exception {
        UpdateTraineeTrainersDto dto = UpdateTraineeTrainersDto.builder().username("").trainers(List.of()).build();

        mockMvc.perform(put("/trainees/update-trainers")
                        .contentType("application/json")
                        .principal(() -> "Dilmurod.Sadyrov")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTraineeTrainings_validRequest_returns200() throws Exception {
        TraineeTrainingsDto dto = TraineeTrainingsDto.builder().username("Dilmurod.Sadyrov").build();
        when(traineeService.getTrainingsByCriteria(any())).thenReturn(List.of());

        mockMvc.perform(get("/trainees/trainings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void getTraineeTrainings_blankUsername_returns400() throws Exception {
        TraineeTrainingsDto dto = TraineeTrainingsDto.builder().username("").build();

        mockMvc.perform(get("/trainees/trainings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
