package kg.biamino.projects.dto;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainingsDisplayInfoTrainee {
    String trainingName;
    LocalDate trainingDate;
    String trainingType;
    int trainingDuration;
    String trainerName;
}
