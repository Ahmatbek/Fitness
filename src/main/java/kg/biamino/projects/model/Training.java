package kg.biamino.projects.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Training {
     Long traineeId;
     Long trainerId;
     String trainingName;
     TrainingType trainingType;
     LocalDate trainingStart;
     int duration;
}
