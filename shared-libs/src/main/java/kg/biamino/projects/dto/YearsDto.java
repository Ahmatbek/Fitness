package kg.biamino.projects.dto;

import lombok.*;

import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class YearsDto  {
    int year;
    List<MonthDto> monthDtoList;
}
