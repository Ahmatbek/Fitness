package kg.biamino.projects.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrainerTrainingsDto {
    @NotBlank
    String username;
    LocalDate from;
    LocalDate to;
    String traineeName;
}
