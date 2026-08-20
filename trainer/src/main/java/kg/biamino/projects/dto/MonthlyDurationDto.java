package kg.biamino.projects.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Setter
@Getter
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