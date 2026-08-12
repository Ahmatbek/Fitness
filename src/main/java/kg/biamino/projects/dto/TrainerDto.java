package kg.biamino.projects.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.ToString;

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
