package kg.biamino.projects.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.experimental.FieldDefaults;


import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.Builder;
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class UserCredentialsDto {
    @NotBlank(message = "username can't be blank")
    String username;
    @NotBlank(message = "password can't be blank")
    String password;
}
