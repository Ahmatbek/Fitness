package kg.biamino.projects.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTraineeTrainersDto {
    @NotBlank(message = "username cant be blank")
    String username;
    List<String> trainers;

}
