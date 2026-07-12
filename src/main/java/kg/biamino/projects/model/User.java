package kg.biamino.projects.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

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