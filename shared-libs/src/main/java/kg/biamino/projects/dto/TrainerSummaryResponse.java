package kg.biamino.projects.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainerSummaryResponse {
    String username;
    String firstName;
    String lastName;
    Boolean status;
    List<YearsDto> yearsDtoList;
}
