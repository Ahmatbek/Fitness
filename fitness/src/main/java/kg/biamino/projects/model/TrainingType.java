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
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

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