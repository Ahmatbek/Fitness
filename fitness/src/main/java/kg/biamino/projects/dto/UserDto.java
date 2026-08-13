package kg.biamino.projects.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@SuperBuilder
public class UserDto {
    @NotBlank(message = "first name can't be empty")
    String firstName;
    @NotBlank(message = "last name can't be empty")
    String lastName;
}
