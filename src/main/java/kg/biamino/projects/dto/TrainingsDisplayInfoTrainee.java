package kg.biamino.projects.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class TrainingsDisplayInfoTrainee {
    String trainingName;
    LocalDate trainingDate;
    String trainingType;
    int trainingDuration;
    String trainerName;
}
