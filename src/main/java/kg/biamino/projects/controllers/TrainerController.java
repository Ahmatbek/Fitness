package kg.biamino.projects.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kg.biamino.projects.auth.AuthHandler;
import kg.biamino.projects.dto.*;
import kg.biamino.projects.service.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("trainers")
@Tag(name = "Trainer", description = "Trainer registration and profile management")
public class TrainerController{

    private final TrainerService trainerService;
    private final AuthHandler authHandler;

    public TrainerController(TrainerService trainerService, AuthHandler authHandler) {
        this.trainerService = trainerService;
        this.authHandler = authHandler;
    }

    @PostMapping
    @Operation(summary = "register a new trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "trainer registered, generated username and password returned"),
            @ApiResponse(responseCode = "400", description = "required fields missing or invalid"),
            @ApiResponse(responseCode = "404", description = "specialization (training type) doesnt exist")
    })
    public ResponseEntity<?> addTrainer(@Valid @RequestBody TrainerDto trainer) {
        return ResponseEntity.ok(trainerService.createTrainer(trainer));
    }

    @GetMapping
    @Operation(summary = "get trainer profile by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully found"),
            @ApiResponse(responseCode = "401", description = "authentication failed"),
            @ApiResponse(responseCode = "404", description = "trainer with this username doesnt exist")
    })
    public ResponseEntity<?> findByUsername(@Parameter(description = "trainer's username") @RequestParam(name="username") String username, HttpServletRequest req) {
        authHandler.handle(req);
        return ResponseEntity.ok(trainerService.findByUsername(username));
    }

    @PutMapping
    @Operation(summary = "update trainer profile (specialization is read only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "trainer profile updated"),
            @ApiResponse(responseCode = "400", description = "required fields missing or invalid"),
            @ApiResponse(responseCode = "401", description = "authentication failed"),
            @ApiResponse(responseCode = "403", description = "not allowed to modify another user's profile"),
            @ApiResponse(responseCode = "404", description = "trainer with this username doesnt exist")
    })
    public ResponseEntity<?> updateTrainer(@Valid @RequestBody UpdateTrainerDto trainer, HttpServletRequest req) {
        String authUsername = authHandler.handle(req);
        return ResponseEntity.ok(trainerService.updateTrainer(trainer, authUsername));
    }

    @PatchMapping
    @Operation(summary = "activate or deactivate trainer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "status updated"),
            @ApiResponse(responseCode = "400", description = "required fields missing or invalid"),
            @ApiResponse(responseCode = "401", description = "authentication failed"),
            @ApiResponse(responseCode = "404", description = "trainer with this username doesnt exist")
    })
    public ResponseEntity<?> changeStatus(@Valid @RequestBody ChangeStatusDto trainer, HttpServletRequest req) {
        String authUsername = authHandler.handle(req);
        trainerService.changeStatusTrainer(trainer, authUsername);
        return ResponseEntity.ok().build();
    }

    @GetMapping("trainings")
    @Operation(summary = "get trainer's trainings filtered by period and trainee name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "trainings list returned"),
            @ApiResponse(responseCode = "400", description = "required fields missing or invalid"),
            @ApiResponse(responseCode = "401", description = "authentication failed")
    })
    public ResponseEntity<?> getTraineeTrainings(@Valid @RequestBody TrainerTrainingsDto traineeTrainingsDto, HttpServletRequest request) {
        authHandler.handle(request);
        return ResponseEntity.ok(trainerService.getTrainingsByCriteria(traineeTrainingsDto));
    }
}
