package kg.biamino.projects.dto;


import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainerUsernameDto extends UserDto{
    String username;
    String specialization;
}
