package kg.biamino.projects.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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
     @ToString.Exclude
     Trainee trainee;
     @ManyToOne
     @JoinColumn(name = "trainer_id", nullable = false)
     @ToString.Exclude
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