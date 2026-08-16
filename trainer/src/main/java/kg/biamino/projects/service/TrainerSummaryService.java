package kg.biamino.projects.service;

import kg.biamino.projects.dto.TrainerWorkloadRequest;
import kg.biamino.projects.dto.TrainerSummaryResponse;

public interface TrainerSummaryService {
    void updateTrainerWorkload(TrainerWorkloadRequest trainerWorkloadRequest);

    TrainerSummaryResponse getMonthlySummaryByTrainerUsername(String username);
}
