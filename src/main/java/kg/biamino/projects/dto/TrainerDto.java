package kg.biamino.projects.dto;


import jakarta.validation.constraints.NotBlank;
import kg.biamino.projects.model.TrainingType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)

public class TrainerDto extends UserDto {
    Long id;
    @NotBlank(message = "specialization can't be blank")
    String specialization;
}
