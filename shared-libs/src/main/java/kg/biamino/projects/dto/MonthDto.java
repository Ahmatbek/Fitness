package kg.biamino.projects.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
