package kg.biamino.projects.dto;

import lombok.experimental.FieldDefaults;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import java.util.List;
import java.util.Map;
import lombok.Builder;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseBody {
    String title;
    Map<String, List<String>> details;
}
