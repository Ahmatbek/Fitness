package kg.biamino.projects.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@FieldDefaults(level= AccessLevel.PRIVATE)
@Getter
@Setter
public class TrainerTraineesListDto extends UserDto {
    String username;
    String specialization;
    Boolean isActive;
    List<TraineeUsernameDto> trainees;

}
