package kg.biamino.projects.service;

import kg.biamino.projects.dto.TrainerWorkloadRequest;

import org.springframework.messaging.handler.annotation.Header;

import kg.biamino.projects.dto.TrainerSummaryResponse;

public interface TrainerSummaryService {
    void updateTrainerWorkload(TrainerWorkloadRequest trainerWorkloadRequest, @Header ("transactionId") String transactionId);

    TrainerSummaryResponse getMonthlySummaryByTrainerUsername(String username);
}
