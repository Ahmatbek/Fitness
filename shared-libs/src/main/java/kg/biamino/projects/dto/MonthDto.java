package kg.biamino.projects.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Setter
public class MonthDto {
    int month;
    int trainingSummaryDuration;
}
