package kg.biamino.projects.controllers;

import kg.biamino.projects.service.TrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("trainings")
public class TrainingController {

    private final TrainingService trainingService;
    public TrainingController(final TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @GetMapping
    public ResponseEntity<?> getAllTrainings() {
        return ResponseEntity.ok(trainingService.getAllTrainings());
    }


}
