package kg.biamino.projects.dto;

import kg.biamino.projects.model.TrainingType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TrainingDto {
    Long traineeId;
    Long trainerId;
    String trainingName;
    TrainingType trainingType;
    LocalDate trainingStart;
    int duration;
}
