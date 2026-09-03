package kg.biamino.projects.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.biamino.projects.dto.TrainingCreatedDto;
import kg.biamino.projects.dto.TrainingDto;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.service.TrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("trainings")
@Tag(name = "Training", description = "Training scheduling (create only, no update or delete)")
public class TrainingController {

    private final TrainingService trainingService;
    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }


    @PostMapping
    @Operation(summary = "add a new training")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "training created, id returned"),
            @ApiResponse(responseCode = "400", description = "required fields missing or invalid"),
            @ApiResponse(responseCode = "401", description = "authentication failed"),
            @ApiResponse(responseCode = "404", description = "trainee, trainer or training type doesnt exist")
    })
    @PreAuthorize("hasAuthority('TRAINER')")
    public ResponseEntity<TrainingCreatedDto> createTraining(@Valid @RequestBody TrainingDto trainingDto) {
        Training training = trainingService.createTraining(trainingDto);
        return ResponseEntity.ok(TrainingCreatedDto.builder().id(training.getId()).build());
    }

    @PreAuthorize("hasAuthority('TRAINER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTraining(@PathVariable  Long id){
        trainingService.deleteTrainingById(id);
        return ResponseEntity.noContent().build();
    }


}
