package kg.biamino.projects.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateTrainerDto extends UserDto{
    @NotBlank
    String username;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String specialization;
    @NotNull
    Boolean isActive;
}
