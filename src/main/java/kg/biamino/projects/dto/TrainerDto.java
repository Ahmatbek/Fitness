package kg.biamino.projects.dto;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class TrainerDto extends UserDto {
    String specialization;

    public TrainerDto(String firstName,String lastName,String specialization) {
        super(firstName,lastName);
        this.specialization = specialization;
    }

}
