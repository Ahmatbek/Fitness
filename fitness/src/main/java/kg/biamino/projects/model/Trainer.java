package kg.biamino.projects.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinTable;


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
             joinColumns = @JoinColumn(name = "trainer_id",
                                        foreignKey = @ForeignKey(name="trainer_foreign_key") ),
             inverseJoinColumns = @JoinColumn(name = "trainee_id",
                                             foreignKey = @ForeignKey(name = "trainee_foreign_key"))

     )
     @ToString.Exclude
     @OnDelete(action = OnDeleteAction.CASCADE)
     List<Trainee> trainees;


}