package kg.biamino.projects.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kg.biamino.projects.auth.AuthHandler;
import kg.biamino.projects.service.TrainingTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("training-types")
@Tag(name = "Training Type", description = "Read-only reference data for training types")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;
    private final AuthHandler authHandler;

    public TrainingTypeController(TrainingTypeService trainingTypeService, AuthHandler authHandler) {
        this.trainingTypeService = trainingTypeService;
        this.authHandler = authHandler;
    }

    @GetMapping
    @Operation(summary = "get all training types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "training types list returned"),
            @ApiResponse(responseCode = "401", description = "authentication failed")
    })
    ResponseEntity<?> getAllTrainingTypes(HttpServletRequest req) {
        authHandler.handle(req);
        return ResponseEntity.ok(trainingTypeService.findAll());
    }
}
