package kg.biamino.projects.dto;

import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.ToString;
import java.time.LocalDate;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(callSuper = true)
public class TraineeTrainersListDto extends UserDto {
    String address;
    LocalDate dateOfBirth;
    boolean isActive;
    List<TrainerUsernameDto> trainers;
}
