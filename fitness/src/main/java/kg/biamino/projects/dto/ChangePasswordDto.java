package kg.biamino.projects.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordDto (
        @NotBlank(message = "first name can't be empty")String username,
        @NotBlank(message = "old Password can't be empty")String oldPassword,
        @NotBlank(message = "new Password can't be empty")
        @Pattern(
                regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$",
                message = "password must be at least 8 characters and contain a digit, lowercase and uppercase letter"
        )
        String newPassword) {
}
