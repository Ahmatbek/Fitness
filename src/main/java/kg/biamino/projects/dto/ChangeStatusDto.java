package kg.biamino.projects.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level= AccessLevel.PRIVATE)
public class ChangeStatusDto {
    @NotBlank(message = "username cant be blank")
    String username;
    @NotNull(message = "status cant be null")
    Boolean isActive;
}
