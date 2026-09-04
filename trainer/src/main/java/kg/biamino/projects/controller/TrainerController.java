package kg.biamino.projects.controller;

import kg.biamino.projects.dto.TrainerSummaryResponse;
import kg.biamino.projects.service.TrainerSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/trainers")
public class TrainerController {
    private final TrainerSummaryService trainerService;

    public TrainerController(TrainerSummaryService trainerService) {
        this.trainerService = trainerService;
    }


    @GetMapping("/{username}/summary")
    public ResponseEntity<TrainerSummaryResponse> getSummary(@PathVariable String username) {
        return ResponseEntity.ok(trainerService.getMonthlySummaryByTrainerUsername(username));

    }
}