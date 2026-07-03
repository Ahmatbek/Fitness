package kg.biamino.projects.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name="trainings")
public class Training {
     @Id
     @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trainings")
     @SequenceGenerator(name = "trainings", sequenceName = "training_seq")
     Long id;
     @ManyToOne
     @JoinColumn(name = "trainee_id", nullable = false)
     Trainee trainee;
     @ManyToOne
     @JoinColumn(name = "trainer_id", nullable = false)
     Trainer trainer;
     @Column(nullable = false)
     @NotNull
     String trainingName;
     @ManyToOne
     @JoinColumn(name="training_type_id")
     TrainingType trainingType;
     @Column(nullable = false)
     @NotNull
     LocalDate date;
     @Column(nullable = false)
     @NotNull
     Integer duration;
}