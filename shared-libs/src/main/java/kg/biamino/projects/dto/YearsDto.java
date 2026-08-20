package kg.biamino.projects.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

import java.util.List;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class YearsDto  {
    int year;
    List<MonthDto> monthDtoList;
}
