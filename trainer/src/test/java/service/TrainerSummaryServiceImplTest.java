package service;

import kg.biamino.projects.enums.ActionType;
import kg.biamino.projects.dto.MonthDto;
import kg.biamino.projects.dto.TrainerSummaryResponse;
import kg.biamino.projects.dto.TrainerWorkloadRequest;
import kg.biamino.projects.dto.YearsDto;
import kg.biamino.projects.dto.MonthlyDurationDto;
import kg.biamino.projects.exception.TrainerNotFoundException;
import kg.biamino.projects.model.TrainerSummary;
import kg.biamino.projects.repository.TrainerSummaryRepository;
import kg.biamino.projects.service.impl.TrainerSummaryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerSummaryServiceImplTest {

    @Mock
    private TrainerSummaryRepository trainerSummaryRepository;

    private TrainerSummaryServiceImpl trainerSummaryService;

    @BeforeEach
    void setUp() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        trainerSummaryService = new TrainerSummaryServiceImpl(trainerSummaryRepository, validator);
    }

    private TrainerWorkloadRequest workloadRequest(ActionType actionType) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("Bekzat.Isakov");
        request.setTrainerFirstName("Bekzat");
        request.setTrainerLastName("Isakov");
        request.setActive(true);
        request.setTrainingDate(LocalDate.of(2026, 8, 25));
        request.setTrainingDuration(60);
        request.setActionType(actionType);
        return request;
    }

    @Test
    void updateTrainerWorkload_add_savesNewRecord() {
        TrainerWorkloadRequest request = workloadRequest(ActionType.ADD);

        trainerSummaryService.updateTrainerWorkload(request);

        ArgumentCaptor<TrainerSummary> captor = ArgumentCaptor.forClass(TrainerSummary.class);
        verify(trainerSummaryRepository).save(captor.capture());
        TrainerSummary saved = captor.getValue();
        assertEquals("Bekzat.Isakov", saved.getUsername());
        assertEquals(60, saved.getDuration());
        assertEquals(request.getTrainingDate(), saved.getTrainingDate());
    }

    @Test
    void updateTrainerWorkload_deleteExistingRecord_savesNegativeDuration() {
        TrainerWorkloadRequest request = workloadRequest(ActionType.DELETE);

        trainerSummaryService.updateTrainerWorkload(request);

        ArgumentCaptor<TrainerSummary> captor = ArgumentCaptor.forClass(TrainerSummary.class);
        verify(trainerSummaryRepository).save(captor.capture());
        assertEquals(-60, captor.getValue().getDuration());
    }

    @Test
    void getMonthlySummaryByTrainerUsername_found_returnsAggregatedSummary() {
        TrainerSummary profile = new TrainerSummary();
        profile.setUsername("Bekzat.Isakov");
        profile.setFirstName("Bekzat");
        profile.setLastName("Isakov");
        profile.setActive(true);

        when(trainerSummaryRepository.findByUsername("Bekzat.Isakov"))
                .thenReturn(List.of(profile));
        when(trainerSummaryRepository.getMonthlySummary("Bekzat.Isakov"))
                .thenReturn(List.of(new MonthlyDurationDto(2026, 8, 120L)));

        TrainerSummaryResponse response = trainerSummaryService.getMonthlySummaryByTrainerUsername("Bekzat.Isakov");

        assertEquals("Bekzat.Isakov", response.getUsername());
        assertEquals("Bekzat", response.getFirstName());
        assertEquals(Boolean.TRUE, response.getStatus());
        assertEquals(1, response.getYearsDtoList().size());

        YearsDto yearsDto = response.getYearsDtoList().getFirst();
        assertEquals(2026, yearsDto.getYear());
        MonthDto monthDto = yearsDto.getMonthDtoList().getFirst();
        assertEquals(8, monthDto.getMonth());
        assertEquals(120L, monthDto.getTrainingSummaryDuration());
    }

    @Test
    void getMonthlySummaryByTrainerUsername_notFound_throwsTrainerNotFoundException() {
        when(trainerSummaryRepository.findByUsername("unknown")).thenReturn(List.of());

        assertThrows(TrainerNotFoundException.class,
                () -> trainerSummaryService.getMonthlySummaryByTrainerUsername("unknown"));
    }


    @Test
    void updateTrainerWorkload_deletePastTraining_throwsIllegalArgumentException() {
        TrainerWorkloadRequest request = workloadRequest(ActionType.DELETE);
        request.setTrainingDate(LocalDate.of(2025, 8, 10));

        assertThrows(IllegalArgumentException.class, ()-> trainerSummaryService.updateTrainerWorkload(request));
    }

    @Test
    void updateTrainerWorkload_deleteTrainingWithZeroDuration_throwsIllegalArgumentException() {
        TrainerWorkloadRequest request = workloadRequest(ActionType.DELETE);
        request.setTrainingDuration(0);

        assertThrows(IllegalArgumentException.class, ()-> trainerSummaryService.updateTrainerWorkload(request));
    }
    @Test
    void updateTrainerWorkload_deleteTrainingWithNullUsername_throwsIllegalArgumentException() {
        TrainerWorkloadRequest request = workloadRequest(ActionType.DELETE);
        request.setTrainerUsername(null);

        assertThrows(IllegalArgumentException.class, ()-> trainerSummaryService.updateTrainerWorkload(request));
    }

    @Test
    void updateTrainerWorkload_deleteTrainingWithNullFirstName_throwsIllegalArgumentException() {
        TrainerWorkloadRequest request = workloadRequest(ActionType.DELETE);
        request.setTrainerFirstName(null);
        assertThrows(IllegalArgumentException.class, ()-> trainerSummaryService.updateTrainerWorkload(request));
    }

    @Test
    void updateTrainerWorkload_deleteTrainingWithNullLastName_throwsIllegalArgumentException() {
        TrainerWorkloadRequest request = workloadRequest(ActionType.DELETE);
        request.setTrainerLastName(null);
        assertThrows(IllegalArgumentException.class, ()-> trainerSummaryService.updateTrainerWorkload(request));
    }

    @Test
    void updateTrainerWorkload_deleteTrainingWithNullActionType_throwsIllegalArgumentException() {
        TrainerWorkloadRequest request = workloadRequest(ActionType.DELETE);
        request.setActionType(null);
        assertThrows(IllegalArgumentException.class, ()-> trainerSummaryService.updateTrainerWorkload(request));
    }
    
    @Test
    void updateTrainerWorkload_deleteTrainingWithNullDate_throwsIllegalArgumentException() {
        TrainerWorkloadRequest request = workloadRequest(ActionType.DELETE);
        request.setTrainingDate(null);
        assertThrows(IllegalArgumentException.class, () -> trainerSummaryService.updateTrainerWorkload(request));
    }
}
