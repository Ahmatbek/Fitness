package kg.biamino.projects.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name="training_types")
public class TrainingType {
     @Id
     @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trainingType")
     @SequenceGenerator(name = "trainingType", sequenceName = "training_type_seq")
     Long id;

     @Column(nullable = false)
     @NotNull
     String name;

     @OneToMany(mappedBy = "trainingType", cascade = CascadeType.ALL, orphanRemoval = true)
     List<Training> trainings;

     @OneToMany(mappedBy = "specialization", cascade = CascadeType.ALL, orphanRemoval = true)
     List<Trainer> trainers;

     public TrainingType(String name) {
          this.name = name;
     }
}