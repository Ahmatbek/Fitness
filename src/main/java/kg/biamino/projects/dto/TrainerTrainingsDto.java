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
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainerTrainingsDto {
    @NotBlank
    String username;
    LocalDate from;
    LocalDate to;
    String traineeName;
}
