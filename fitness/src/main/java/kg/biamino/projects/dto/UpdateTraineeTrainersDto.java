package kg.biamino.projects.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.experimental.FieldDefaults;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.Builder;
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
