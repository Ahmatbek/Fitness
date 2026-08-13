package kg.biamino.projects;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class YearsDto  {
    int year;
    List<MonthDto> monthDtoList;
}
