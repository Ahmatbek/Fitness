package kg.biamino.projects.controller;

import jakarta.validation.Valid;
import kg.biamino.projects.TrainerWorkloadRequest;
import kg.biamino.projects.TrainerSummaryResponse;
import kg.biamino.projects.service.TrainerSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainers")
public class TrainerController {
    private final TrainerSummaryService trainerService;

    public TrainerController(TrainerSummaryService trainerService) {
        this.trainerService = trainerService;
    }


    @PostMapping("/workload")
    public ResponseEntity<Void> acceptWorkload(@RequestBody @Valid TrainerWorkloadRequest request) {
        trainerService.updateTrainerWorkload(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/summary")
    public ResponseEntity<TrainerSummaryResponse> getSummary(@PathVariable String username) {
        return ResponseEntity.ok(trainerService.getMonthlySummaryByTrainerUsername(username));
    }
}