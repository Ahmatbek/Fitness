package kg.biamino.projects.model;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.ToString;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name="users")
public class User {
     @Id
     @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usersd")
     @SequenceGenerator(name = "usersd", sequenceName="users_seq")
     Long id;
     @Column(nullable = false)
     @NotNull
     String firstName;
     @NotNull
     @Column(nullable = false)
     String lastName;
     @NotNull
     @Column(nullable = false, unique = true)
     String username;
     @NotNull
     @Column(nullable = false)
     String password;
     @Column(nullable = false)
     @NotNull
     Boolean isActive;


}