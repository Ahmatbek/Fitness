package kg.biamino.projects.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class User {
     Long id;
     String firstName;
     String lastName;
     String username;
     String password;
     Boolean isActive = true;
}
