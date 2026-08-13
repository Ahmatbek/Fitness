package service;

import kg.biamino.projects.ActionType;
import kg.biamino.projects.TrainerWorkloadRequest;
import kg.biamino.projects.exception.MicroServiceNotWorkingException;
import kg.biamino.projects.service.impl.WorkloadServiceClientFallback;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkloadServiceClientFallbackTest {

    private final WorkloadServiceClientFallback fallback = new WorkloadServiceClientFallback();

    @Test
    void updateWorkload_trainerServiceUnavailable_throwsMicroServiceNotWorkingException() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("Bekzat.Isakov");
        request.setActionType(ActionType.ADD);
        request.setTrainingDate(LocalDate.now());
        request.setTrainingDuration(60);

        assertThrows(MicroServiceNotWorkingException.class, () -> fallback.updateWorkload(request));
    }

    @Test
    void getMonthlyDuration_trainerServiceUnavailable_throwsMicroServiceNotWorkingException() {
        assertThrows(MicroServiceNotWorkingException.class, () -> fallback.getMonthlyDuration("Bekzat.Isakov"));
    }
}
