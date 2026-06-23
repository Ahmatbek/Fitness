package kg.biamino.projects.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class Training {
     Long traineeId;
     Long trainerId;
     String trainingName;
     TrainingType trainingType;
     LocalDate trainingStart;
     int duration;
}
