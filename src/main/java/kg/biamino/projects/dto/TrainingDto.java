package kg.biamino.projects.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kg.biamino.projects.model.TrainingType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TrainingDto {
    @NotBlank(message = "Trainee username cant be blank")
    String traineeUsername;
    @NotBlank(message = "Trainer username cant be blank")
    String trainerUsername;
    @NotBlank(message = "Training name cant be blank")
    String trainingName;
    @NotBlank(message = "Training type cant be blank")
    String trainingType;
    @NotNull(message = "Date cant be null")
    LocalDate trainingStart;
    @NotNull
    int duration;
}
