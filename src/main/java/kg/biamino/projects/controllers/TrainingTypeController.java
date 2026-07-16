package kg.biamino.projects.controllers;


import jakarta.servlet.http.HttpServletRequest;
import kg.biamino.projects.auth.AuthHandler;
import kg.biamino.projects.service.TrainingTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("training-types")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;
    private final AuthHandler authHandler;

    public TrainingTypeController(TrainingTypeService trainingTypeService, AuthHandler authHandler) {
        this.trainingTypeService = trainingTypeService;
        this.authHandler = authHandler;
    }

    @GetMapping
    ResponseEntity<?> getAllTrainingTypes(HttpServletRequest req) {
        authHandler.handle(req);
        return ResponseEntity.ok(trainingTypeService.findAll());
    }
}
