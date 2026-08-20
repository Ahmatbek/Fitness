package kg.biamino.projects.service.impl;

import kg.biamino.projects.dto.TrainerSummaryResponse;
import kg.biamino.projects.dto.TrainerWorkloadRequest;
import kg.biamino.projects.exception.MicroServiceNotWorkingException;
import kg.biamino.projects.service.WorkloadServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class WorkloadServiceClientFallback implements WorkloadServiceClient {

    @Override
    public ResponseEntity<Void> updateWorkload(TrainerWorkloadRequest request) {
        log.warn("Trainer service unavailable, could not report {} workload for trainer {}",
                request.getActionType(), request.getTrainerUsername());
        throw new MicroServiceNotWorkingException("Trainer service unavailable");
    }

    @Override
    public ResponseEntity<TrainerSummaryResponse> getMonthlyDuration(String username) {
        log.warn("Trainer service unavailable, returning empty summary for trainer {}", username);
        throw new MicroServiceNotWorkingException("Trainer service unavailable");
    }
}
