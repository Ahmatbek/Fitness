package kg.biamino.projects.dto;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MonthlyDurationDto {
     Integer year;
     Integer month;
     Integer totalDuration;

}