package kg.biamino.projects.controllers;

import jakarta.validation.Valid;
import kg.biamino.projects.dto.TrainerDto;
import kg.biamino.projects.service.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainers")
public class TrainerController{

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @GetMapping
    public ResponseEntity<?> getAllTrainers() {
        return ResponseEntity.ok(trainerService.getAllTrainers());
    }

    @PostMapping
    public ResponseEntity<?> addTrainer(@Valid @RequestBody TrainerDto trainer) {
        return ResponseEntity.ok(trainerService.createTrainer(trainer));
    }
}
