package kg.biamino.projects.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.experimental.FieldDefaults;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TraineeTrainingsDto {
    @NotBlank
    String username;
    LocalDate from;
    LocalDate to;
    String trainerName;
    String trainingType;

}
