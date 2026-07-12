package kg.biamino.projects.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level= AccessLevel.PRIVATE)
@ToString
public class TraineeDto extends UserDto{
    String address;
    LocalDate dateOfBirth;

    public TraineeDto(String firstname, String lastname, String address, LocalDate dateOfBirth) {
        super(firstname, lastname);
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }
}
