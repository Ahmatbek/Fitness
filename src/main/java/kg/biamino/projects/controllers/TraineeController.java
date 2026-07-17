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
import kg.biamino.projects.service.TraineeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("trainees")
@Tag(name = "Trainee", description = "Trainee registration and profile management")
public class TraineeController {

    private final TraineeService traineeService;
    private final AuthHandler authHandler;

    public TraineeController(TraineeService traineeService, AuthHandler authHandler) {
        this.traineeService = traineeService;
        this.authHandler = authHandler;
    }


    @PostMapping
    @Operation(summary = "register a new trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "trainee registered, generated username and password returned"),
            @ApiResponse(responseCode = "400", description = "required fields missing or invalid")
    })
    public ResponseEntity<?> registerTrainee(@Valid @RequestBody TraineeDto traineeDto) {
       return ResponseEntity.ok(traineeService.createTrainee(traineeDto));
    }

    @GetMapping()
    @Operation(summary = "return trainee by their username")
    @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "successfully found"),
            @ApiResponse(responseCode = "400", description = "user pasted incorrect data"),
            @ApiResponse(responseCode = "404", description = "trainee with this username doesnt exist")
    })
    public ResponseEntity<?> getTraineeByUsername(@Parameter(description = "trainee's username") @RequestParam(name = "username") String username, HttpServletRequest request) {
        authHandler.handle(request);
        return ResponseEntity.ok(traineeService.findByUsername(username));
    }

    @PutMapping
    @Operation(summary = "update trainee profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "trainee profile updated"),
            @ApiResponse(responseCode = "400", description = "required fields missing or invalid"),
            @ApiResponse(responseCode = "401", description = "authentication failed"),
            @ApiResponse(responseCode = "403", description = "not allowed to modify another user's profile"),
            @ApiResponse(responseCode = "404", description = "trainee with this username doesnt exist")
    })
    public ResponseEntity<?> updateTraineeByUsername(@Valid @RequestBody UpdateTraineeDto traineeDto, HttpServletRequest request) {
        String username= authHandler.handle(request);
        return ResponseEntity.ok(traineeService.updateTrainee(traineeDto, username));
    }

    @DeleteMapping
    @Operation(summary = "delete trainee profile (hard delete, cascades to their trainings)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "trainee deleted"),
            @ApiResponse(responseCode = "401", description = "authentication failed"),
            @ApiResponse(responseCode = "403", description = "not allowed to delete another user's profile"),
            @ApiResponse(responseCode = "404", description = "trainee with this username doesnt exist")
    })
    public ResponseEntity<Void> deleteTraineeByUsername(@Parameter(description = "trainee's username") @RequestParam(name = "username") String username, HttpServletRequest request) {
        String authUsername=  authHandler.handle(request);
        traineeService.deleteTraineeByUsername(username, authUsername);
        return ResponseEntity.ok().build();
    }


    @PatchMapping
    @Operation(summary = "activate or deactivate trainee profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "status updated"),
            @ApiResponse(responseCode = "400", description = "required fields missing or invalid"),
            @ApiResponse(responseCode = "401", description = "authentication failed"),
            @ApiResponse(responseCode = "404", description = "trainee with this username doesnt exist")
    })
    public ResponseEntity<?> changeStatus(@Valid @RequestBody ChangeStatusDto trainer, HttpServletRequest req) {
        String authUsername = authHandler.handle(req);
        traineeService.changeStatusTrainer(trainer, authUsername);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/not-assigned")
    @Operation(summary = "get active trainers not yet assigned to the trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list of not-assigned trainers returned"),
            @ApiResponse(responseCode = "401", description = "authentication failed"),
            @ApiResponse(responseCode = "404", description = "trainee with this username doesnt exist")
    })
    public ResponseEntity<?> getTrainersNotAssignedByTraineeByUsername(@Parameter(description = "trainee's username") @RequestParam(name = "username") String username, HttpServletRequest request) {
        authHandler.handle(request);
        return ResponseEntity.ok(traineeService.findNotAssignedTrainersByUsername(username));
    }

    @PutMapping("/update-trainers")
    @Operation(summary = "replace the trainee's assigned trainer list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "updated trainer list returned"),
            @ApiResponse(responseCode = "400", description = "required fields missing or invalid"),
            @ApiResponse(responseCode = "401", description = "authentication failed"),
            @ApiResponse(responseCode = "403", description = "not allowed to modify another user's trainer list"),
            @ApiResponse(responseCode = "404", description = "trainee or one of the trainers doesnt exist")
    })
    public ResponseEntity<?> updateTraineeTrainers(@Valid @RequestBody UpdateTraineeTrainersDto trainerDto, HttpServletRequest request) {
        String authUsername = authHandler.handle(request);
        return ResponseEntity.ok(traineeService.updateTrainersByUsername(trainerDto, authUsername));
    }

    @GetMapping("trainings")
    @Operation(summary = "get trainee's trainings filtered by period, trainer name and training type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "trainings list returned"),
            @ApiResponse(responseCode = "400", description = "required fields missing or invalid"),
            @ApiResponse(responseCode = "401", description = "authentication failed")
    })
    public ResponseEntity<?> getTraineeTrainings(@Valid @RequestBody TraineeTrainingsDto traineeTrainingsDto, HttpServletRequest request) {
        authHandler.handle(request);
        return ResponseEntity.ok(traineeService.getTrainingsByCriteria(traineeTrainingsDto));
    }










}
