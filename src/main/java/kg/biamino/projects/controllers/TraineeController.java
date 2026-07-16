package kg.biamino.projects.controllers;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kg.biamino.projects.auth.AuthHandler;
import kg.biamino.projects.dto.TraineeDto;
import kg.biamino.projects.dto.UpdateTraineeDto;
import kg.biamino.projects.service.TraineeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("trainees")
public class TraineeController {

    private final TraineeService traineeService;
    private final AuthHandler authHandler;

    public TraineeController(TraineeService traineeService, AuthHandler authHandler) {
        this.traineeService = traineeService;
        this.authHandler = authHandler;
    }


    @PostMapping
    public ResponseEntity<?> registerTrainee(@Valid @RequestBody TraineeDto traineeDto) {
       return ResponseEntity.ok(traineeService.createTrainee(traineeDto));
    }

    @GetMapping()
    public ResponseEntity<?> getTraineeByUsername(@RequestParam(name = "username") String username, HttpServletRequest request) {
        authHandler.handle(request);
        return ResponseEntity.ok(traineeService.findByUsername(username));
    }

    @PutMapping
    public ResponseEntity<?> updateTraineeByUsername(@Valid @RequestBody UpdateTraineeDto traineeDto, HttpServletRequest request) {
        String username= authHandler.handle(request);
        return ResponseEntity.ok(traineeService.updateTrainee(traineeDto, username));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTraineeByUsername(@RequestParam(name = "username") String username, HttpServletRequest request) {
        String authUsername=  authHandler.handle(request);
        traineeService.deleteTraineeByUsername(username, authUsername);
        return ResponseEntity.ok().build();
    }










}
