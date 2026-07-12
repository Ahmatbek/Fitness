package kg.biamino.projects.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name="trainers")
public class Trainer {
     @Id
     @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trainers")
     @SequenceGenerator(name = "trainers" ,sequenceName = "trainers_seq")
     Long id;

     @ManyToOne(cascade = CascadeType.ALL)
     @JoinColumn(name = "specialization_id", nullable = false)
     TrainingType specialization;
     @OneToOne(fetch = FetchType.EAGER)
     @OnDelete(action = OnDeleteAction.CASCADE)
     User user;

     @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
     @ToString.Exclude
     List<Training> trainings;

     @ManyToMany
     @JoinTable(
             name = "trainee_traineer",
             joinColumns = @JoinColumn(name = "trainer_id"),
             inverseJoinColumns = @JoinColumn(name = "trainee_id")
     )
     @ToString.Exclude
     List<Trainee> trainees;


}