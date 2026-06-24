package kg.biamino.projects.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
     Long id;
     String firstName;
     String lastName;
     String username;
     String password;
     Boolean isActive;
}
