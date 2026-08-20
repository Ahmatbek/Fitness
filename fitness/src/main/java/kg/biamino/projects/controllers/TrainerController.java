package kg.biamino.projects.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.biamino.projects.dto.*;
import kg.biamino.projects.service.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("trainers")
@Tag(name = "Trainer", description = "Trainer registration and profile management")
public class TrainerController{

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
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
    public ResponseEntity<?> findByUsername(@Parameter(description = "trainer's username") @RequestParam(name="username") String username) {
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
    public ResponseEntity<?> updateTrainer(@Valid @RequestBody UpdateTrainerDto trainer, Principal principal) {
        return ResponseEntity.ok(trainerService.updateTrainer(trainer, principal.getName()));
    }

    @PatchMapping
    @Operation(summary = "activate or deactivate trainer profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "status updated"),
            @ApiResponse(responseCode = "400", description = "required fields missing or invalid"),
            @ApiResponse(responseCode = "401", description = "authentication failed"),
            @ApiResponse(responseCode = "404", description = "trainer with this username doesnt exist")
    })
    public ResponseEntity<?> changeStatus(@Valid @RequestBody ChangeStatusDto trainer, Principal principal) {
        trainerService.changeStatusTrainer(trainer, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("trainings")
    @Operation(summary = "get trainer's trainings filtered by period and trainee name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "trainings list returned"),
            @ApiResponse(responseCode = "400", description = "required fields missing or invalid"),
            @ApiResponse(responseCode = "401", description = "authentication failed")
    })
    public ResponseEntity<?> getTraineeTrainings(@Valid @RequestBody TrainerTrainingsDto traineeTrainingsDto) {
        return ResponseEntity.ok(trainerService.getTrainingsByCriteria(traineeTrainingsDto));
    }

    @GetMapping("/summary")
    @Operation(summary = "get trainer profile by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully found"),
            @ApiResponse(responseCode = "401", description = "authentication failed"),
            @ApiResponse(responseCode = "404", description = "trainer with this username doesnt exist")
    })
    public ResponseEntity<?> getSummaryByUsername(@Parameter(description = "trainer's username") @RequestParam(name="username") String username) {
        return trainerService.getSummaryByUsername(username);
    }
}
