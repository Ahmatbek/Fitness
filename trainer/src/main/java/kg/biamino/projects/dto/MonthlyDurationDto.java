package kg.biamino.projects.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Builder
@Setter
@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MonthlyDurationDto {
     Integer year;
     Integer month;
     Long totalDuration;

     public MonthlyDurationDto(Integer year, Integer month, Long totalDuration) {
          this.year = year;
          this.month = month;
          this.totalDuration = totalDuration;
     }

}