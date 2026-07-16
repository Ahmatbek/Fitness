package kg.biamino.projects.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kg.biamino.projects.auth.AuthHandler;
import kg.biamino.projects.dto.TrainerDto;
import kg.biamino.projects.service.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("trainers")
public class TrainerController{

    private final TrainerService trainerService;
    private final AuthHandler authHandler;

    public TrainerController(TrainerService trainerService, AuthHandler authHandler) {
        this.trainerService = trainerService;
        this.authHandler = authHandler;
    }

    @PostMapping
    public ResponseEntity<?> addTrainer(@Valid @RequestBody TrainerDto trainer) {
        return ResponseEntity.ok(trainerService.createTrainer(trainer));
    }

    @GetMapping
    public ResponseEntity<?> findByUsername(@RequestParam(name="username") String username, HttpServletRequest req) {
        authHandler.handle(req);
        return ResponseEntity.ok(trainerService.findByUsername(username));
    }
}
