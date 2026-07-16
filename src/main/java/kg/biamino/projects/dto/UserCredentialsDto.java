package kg.biamino.projects.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
//@Jacksonized
public class UserCredentialsDto {
    @NotBlank(message = "username can't be blank")
    String username;
    @NotBlank(message = "password can't be blank")
    String password;
}
