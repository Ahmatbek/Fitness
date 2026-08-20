package kg.biamino.projects.model;

import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.ToString;


import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name="trainees")
public class Trainee {
     @Id
     @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trainees")
     @SequenceGenerator(name = "trainees", sequenceName = "trainers_seq")
     Long id;
     LocalDate dateOfBirth;
     String address;
     @OneToOne(fetch = FetchType.EAGER)
     @OnDelete(action = OnDeleteAction.CASCADE)
     User user;

     @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true)
     @ToString.Exclude
     List<Training> trainings;

     @ManyToMany(mappedBy = "trainees")
     @ToString.Exclude
     List<Trainer> trainers;

}