package kg.biamino.projects.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.biamino.projects.dto.*;
import kg.biamino.projects.service.TraineeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@RestController
@RequestMapping("trainees")
@Tag(name = "Trainee", description = "Trainee registration and profile management")
public class TraineeController {

    private final TraineeService traineeService;

    public TraineeController(TraineeService traineeService) {
        this.traineeService = traineeService;
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

    @GetMapping
    @Operation(summary = "return trainee by their username")
    @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "successfully found"),
            @ApiResponse(responseCode = "400", description = "user pasted incorrect data"),
            @ApiResponse(responseCode = "404", description = "trainee with this username doesnt exist")
    })
    public ResponseEntity<?> getTraineeByUsername(@Parameter(description = "trainee's username") @RequestParam(name = "username") String username) {
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
    public ResponseEntity<?> updateTraineeByUsername(@Valid @RequestBody UpdateTraineeDto traineeDto, Principal principal) {
        return ResponseEntity.ok(traineeService.updateTrainee(traineeDto, principal.getName()));
    }

    @DeleteMapping
    @Operation(summary = "delete trainee profile (hard delete, cascades to their trainings)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "trainee deleted"),
            @ApiResponse(responseCode = "401", description = "authentication failed"),
            @ApiResponse(responseCode = "403", description = "not allowed to delete another user's profile"),
            @ApiResponse(responseCode = "404", description = "trainee with this username doesnt exist")
    })
    public ResponseEntity<Void> deleteTraineeByUsername(@Parameter(description = "trainee's username") @RequestParam(name = "username") String username, Principal principal) {
        traineeService.deleteTraineeByUsername(username, principal.getName());
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
    public ResponseEntity<?> changeStatus(@Valid @RequestBody ChangeStatusDto trainer,  Principal principal) {
        traineeService.changeStatusTrainer(trainer, principal.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/not-assigned")
    @Operation(summary = "get active trainers not yet assigned to the trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list of not-assigned trainers returned"),
            @ApiResponse(responseCode = "401", description = "authentication failed"),
            @ApiResponse(responseCode = "404", description = "trainee with this username doesnt exist")
    })
    public ResponseEntity<?> getTrainersNotAssignedByTraineeByUsername(@Parameter(description = "trainee's username") @RequestParam(name = "username") String username) {
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
    public ResponseEntity<?> updateTraineeTrainers(@Valid @RequestBody UpdateTraineeTrainersDto trainerDto, Principal principal) {
        return ResponseEntity.ok(traineeService.updateTrainersByUsername(trainerDto, principal.getName()));
    }

    @GetMapping("trainings")
    @Operation(summary = "get trainee's trainings filtered by period, trainer name and training type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "trainings list returned"),
            @ApiResponse(responseCode = "400", description = "required fields missing or invalid"),
            @ApiResponse(responseCode = "401", description = "authentication failed")
    })
    public ResponseEntity<?> getTraineeTrainings(@Valid @RequestBody TraineeTrainingsDto traineeTrainingsDto) {
        return ResponseEntity.ok(traineeService.getTrainingsByCriteria(traineeTrainingsDto));
    }










}
