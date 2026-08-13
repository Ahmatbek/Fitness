package kg.biamino.projects.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "trainer_summary")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
public class TrainerSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String username;
    String firstName;
    String lastName;
    Boolean active;
    LocalDate trainingDate;
    Integer duration;
}