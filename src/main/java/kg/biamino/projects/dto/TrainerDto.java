package kg.biamino.projects.dto;


import kg.biamino.projects.model.TrainingType;
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
    TrainingType specialization;

    public TrainerDto(String firstName,String lastName,TrainingType specialization) {
        super(firstName,lastName);
        this.specialization = specialization;
    }

}
