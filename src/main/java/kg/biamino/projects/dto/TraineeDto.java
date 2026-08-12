package kg.biamino.projects.dto;

import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import java.time.LocalDate;
import lombok.ToString;

@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class TraineeDto extends UserDto{
    String address;
    LocalDate dateOfBirth;

}
