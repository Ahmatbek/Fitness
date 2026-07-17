package kg.biamino.projects.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class TrainingsDisplayInfoTrainer {
    String trainingName;
    LocalDate trainingDate;
    int trainingDuration;
    String traineeName;
    String trainingType;
}
