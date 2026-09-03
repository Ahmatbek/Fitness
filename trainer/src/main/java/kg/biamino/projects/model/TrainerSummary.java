package kg.biamino.projects.model;

import org.springframework.data.annotation.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import kg.biamino.projects.model.YearsEntity;

import java.util.List;

@Document(collection = "trainer_summary")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@CompoundIndex(name = "firstName_and_lastName_idx", def = "{'firstName' : 1, 'lastName':1}")
public class TrainerSummary {
    @Id
    String id;
    String username;
    String firstName;
    String lastName;
    Boolean status;
    List<YearsEntity> years;
}