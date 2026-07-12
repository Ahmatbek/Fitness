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
    Long id;
    String specialization;

    public TrainerDto( String firstName,String lastName,String specialization) {
        super(firstName,lastName);
        this.specialization = specialization;
    }

    public TrainerDto(Long id, String firstName,String lastName,String specialization) {
        super(firstName,lastName);
        this.specialization = specialization;
        this.id = id;
    }






}
