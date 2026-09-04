package service;

import kg.biamino.projects.dto.TrainerSummaryResponse;
import kg.biamino.projects.dto.TrainerWorkloadRequest;
import kg.biamino.projects.dto.YearsDto;
import kg.biamino.projects.enums.ActionType;
import kg.biamino.projects.exception.TrainerNotFoundException;
import kg.biamino.projects.model.Month;
import kg.biamino.projects.model.TrainerSummary;
import kg.biamino.projects.model.YearsEntity;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerSummaryServiceImplTest {

    private static final LocalDate TRAINING_DATE = LocalDate.now();
    private static final int YEAR = TRAINING_DATE.getYear();
    private static final int MONTH = TRAINING_DATE.getMonthValue();
    private static final String USERNAME = "bekzat.isakov";
    private static final String TX_ID = "test-transaction-id";

    @Mock
    private TrainerSummaryRepository trainerSummaryRepository;

    private TrainerSummaryServiceImpl trainerSummaryService;

    @BeforeEach
    void setUp() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        trainerSummaryService = new TrainerSummaryServiceImpl(trainerSummaryRepository, validator);
    }

    private TrainerWorkloadRequest workloadRequest(ActionType actionType, int duration) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername(USERNAME);
        request.setTrainerFirstName("Bekzat");
        request.setTrainerLastName("Isakov");
        request.setActive(true);
        request.setTrainingDate(TRAINING_DATE);
        request.setTrainingDuration(duration);
        request.setActionType(actionType);
        return request;
    }

    private TrainerSummary existingSummary(List<YearsEntity> years) {
        TrainerSummary summary = new TrainerSummary();
        summary.setUsername(USERNAME);
        summary.setFirstName("Bekzat");
        summary.setLastName("Isakov");
        summary.setStatus(true);
        summary.setYears(years);
        return summary;
    }

    @Test
    void updateTrainerWorkload_newTrainer_createsDocumentWithYearAndMonth() {
        when(trainerSummaryRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        trainerSummaryService.updateTrainerWorkload(workloadRequest(ActionType.ADD, 60), TX_ID);

        ArgumentCaptor<TrainerSummary> captor = ArgumentCaptor.forClass(TrainerSummary.class);
        verify(trainerSummaryRepository).save(captor.capture());
        TrainerSummary saved = captor.getValue();
        assertEquals(USERNAME, saved.getUsername());
        assertEquals("Bekzat", saved.getFirstName());
        assertEquals("Isakov", saved.getLastName());
        assertEquals(Boolean.TRUE, saved.getStatus());
        assertEquals(1, saved.getYears().size());
        YearsEntity years = saved.getYears().get(0);
        assertEquals(YEAR, years.getYear());
        assertEquals(1, years.getMonths().size());
        assertEquals(MONTH, years.getMonths().get(0).getMonth());
        assertEquals(60, years.getMonths().get(0).getDuration());
    }

    @Test
    void updateTrainerWorkload_existingYearAndMonth_addAction_incrementsDuration() {
        TrainerSummary existing = existingSummary(new ArrayList<>(List.of(
                new YearsEntity(YEAR, new ArrayList<>(List.of(new Month(MONTH, 30))))
        )));
        when(trainerSummaryRepository.findByUsername(USERNAME)).thenReturn(Optional.of(existing));

        trainerSummaryService.updateTrainerWorkload(workloadRequest(ActionType.ADD, 20), TX_ID);

        ArgumentCaptor<TrainerSummary> captor = ArgumentCaptor.forClass(TrainerSummary.class);
        verify(trainerSummaryRepository).save(captor.capture());
        List<YearsEntity> years = captor.getValue().getYears();
        assertEquals(1, years.size());
        assertEquals(50, years.get(0).getMonths().get(0).getDuration());
    }

    @Test
    void updateTrainerWorkload_existingYearAndMonth_deleteAction_decrementsDurationFlooredAtZero() {
        TrainerSummary existing = existingSummary(new ArrayList<>(List.of(
                new YearsEntity(YEAR, new ArrayList<>(List.of(new Month(MONTH, 30))))
        )));
        when(trainerSummaryRepository.findByUsername(USERNAME)).thenReturn(Optional.of(existing));

        trainerSummaryService.updateTrainerWorkload(workloadRequest(ActionType.DELETE, 50), TX_ID);

        ArgumentCaptor<TrainerSummary> captor = ArgumentCaptor.forClass(TrainerSummary.class);
        verify(trainerSummaryRepository).save(captor.capture());
        assertEquals(0, captor.getValue().getYears().get(0).getMonths().get(0).getDuration());
    }

    @Test
    void updateTrainerWorkload_existingYearNewMonth_appendsMonthToYear() {
        int otherMonth = MONTH == 1 ? 2 : MONTH - 1;
        TrainerSummary existing = existingSummary(new ArrayList<>(List.of(
                new YearsEntity(YEAR, new ArrayList<>(List.of(new Month(otherMonth, 15))))
        )));
        when(trainerSummaryRepository.findByUsername(USERNAME)).thenReturn(Optional.of(existing));

        trainerSummaryService.updateTrainerWorkload(workloadRequest(ActionType.ADD, 45), TX_ID);

        ArgumentCaptor<TrainerSummary> captor = ArgumentCaptor.forClass(TrainerSummary.class);
        verify(trainerSummaryRepository).save(captor.capture());
        List<YearsEntity> years = captor.getValue().getYears();
        assertEquals(1, years.size());
        assertEquals(2, years.get(0).getMonths().size());
        assertTrue(years.get(0).getMonths().stream()
                .anyMatch(m -> m.getMonth().equals(MONTH) && m.getDuration().equals(45)));
    }

    @Test
    void updateTrainerWorkload_newYear_appendsYearToExistingTrainer() {
        int otherYear = YEAR - 1;
        TrainerSummary existing = existingSummary(new ArrayList<>(List.of(
                new YearsEntity(otherYear, new ArrayList<>(List.of(new Month(MONTH, 15))))
        )));
        when(trainerSummaryRepository.findByUsername(USERNAME)).thenReturn(Optional.of(existing));

        trainerSummaryService.updateTrainerWorkload(workloadRequest(ActionType.ADD, 45), TX_ID);

        ArgumentCaptor<TrainerSummary> captor = ArgumentCaptor.forClass(TrainerSummary.class);
        verify(trainerSummaryRepository).save(captor.capture());
        List<YearsEntity> years = captor.getValue().getYears();
        assertEquals(2, years.size());
        assertTrue(years.stream().anyMatch(y -> y.getYear().equals(YEAR)
                && y.getMonths().stream().anyMatch(m -> m.getMonth().equals(MONTH) && m.getDuration().equals(45))));
    }

    @Test
    void updateTrainerWorkload_repositoryFailure_exceptionPropagatesForDlqRedelivery() {
        when(trainerSummaryRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
        when(trainerSummaryRepository.save(any())).thenThrow(new RuntimeException("mongo down"));

        assertThrows(RuntimeException.class,
                () -> trainerSummaryService.updateTrainerWorkload(workloadRequest(ActionType.ADD, 10), TX_ID));
    }

    @Test
    void updateTrainerWorkload_pastTrainingDate_throwsAndNeverTouchesRepository() {
        TrainerWorkloadRequest request = workloadRequest(ActionType.ADD, 30);
        request.setTrainingDate(LocalDate.of(2020, 1, 1));

        assertThrows(IllegalArgumentException.class,
                () -> trainerSummaryService.updateTrainerWorkload(request, TX_ID));

        verifyNoInteractions(trainerSummaryRepository);
    }

    @Test
    void updateTrainerWorkload_zeroDuration_throwsIllegalArgumentException() {
        TrainerWorkloadRequest request = workloadRequest(ActionType.ADD, 0);

        assertThrows(IllegalArgumentException.class,
                () -> trainerSummaryService.updateTrainerWorkload(request, TX_ID));
    }

    @Test
    void updateTrainerWorkload_blankUsername_throwsIllegalArgumentException() {
        TrainerWorkloadRequest request = workloadRequest(ActionType.ADD, 30);
        request.setTrainerUsername(" ");

        assertThrows(IllegalArgumentException.class,
                () -> trainerSummaryService.updateTrainerWorkload(request, TX_ID));
    }

    @Test
    void updateTrainerWorkload_blankFirstName_throwsIllegalArgumentException() {
        TrainerWorkloadRequest request = workloadRequest(ActionType.ADD, 30);
        request.setTrainerFirstName(" ");

        assertThrows(IllegalArgumentException.class,
                () -> trainerSummaryService.updateTrainerWorkload(request, TX_ID));
    }

    @Test
    void updateTrainerWorkload_blankLastName_throwsIllegalArgumentException() {
        TrainerWorkloadRequest request = workloadRequest(ActionType.ADD, 30);
        request.setTrainerLastName(" ");

        assertThrows(IllegalArgumentException.class,
                () -> trainerSummaryService.updateTrainerWorkload(request, TX_ID));
    }

    @Test
    void updateTrainerWorkload_nullActionType_throwsIllegalArgumentException() {
        TrainerWorkloadRequest request = workloadRequest(ActionType.ADD, 30);
        request.setActionType(null);

        assertThrows(IllegalArgumentException.class,
                () -> trainerSummaryService.updateTrainerWorkload(request, TX_ID));
    }

    @Test
    void updateTrainerWorkload_nullTrainingDate_throwsIllegalArgumentException() {
        TrainerWorkloadRequest request = workloadRequest(ActionType.ADD, 30);
        request.setTrainingDate(null);

        assertThrows(IllegalArgumentException.class,
                () -> trainerSummaryService.updateTrainerWorkload(request, TX_ID));
    }

    @Test
    void getMonthlySummaryByTrainerUsername_found_returnsMappedResponse() {
        TrainerSummary existing = existingSummary(new ArrayList<>(List.of(
                new YearsEntity(YEAR, new ArrayList<>(List.of(new Month(MONTH, 90))))
        )));
        when(trainerSummaryRepository.findByUsername(USERNAME)).thenReturn(Optional.of(existing));

        TrainerSummaryResponse response = trainerSummaryService.getMonthlySummaryByTrainerUsername(USERNAME);

        assertEquals(USERNAME, response.getUsername());
        assertEquals("Bekzat", response.getFirstName());
        assertEquals("Isakov", response.getLastName());
        assertEquals(Boolean.TRUE, response.getStatus());
        assertEquals(1, response.getYearsDtoList().size());
        YearsDto yearsDto = response.getYearsDtoList().get(0);
        assertEquals(YEAR, yearsDto.getYear());
        assertEquals(MONTH, yearsDto.getMonthDtoList().get(0).getMonth());
        assertEquals(90, yearsDto.getMonthDtoList().get(0).getTrainingSummaryDuration());
    }

    @Test
    void getMonthlySummaryByTrainerUsername_notFound_throwsTrainerNotFoundException() {
        when(trainerSummaryRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(TrainerNotFoundException.class,
                () -> trainerSummaryService.getMonthlySummaryByTrainerUsername("unknown"));
    }
}
