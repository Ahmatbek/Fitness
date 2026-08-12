package kg.biamino.projects.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.experimental.FieldDefaults;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.ToString;

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
