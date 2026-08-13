package kg.biamino.projects.service;

import kg.biamino.projects.TrainerWorkloadRequest;
import kg.biamino.projects.TrainerSummaryResponse;

public interface TrainerSummaryService {
    void updateTrainerWorkload(TrainerWorkloadRequest trainerWorkloadRequest);

    TrainerSummaryResponse getMonthlySummaryByTrainerUsername(String username);
}
