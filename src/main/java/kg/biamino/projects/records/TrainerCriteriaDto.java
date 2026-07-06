package kg.biamino.projects.records;

import java.time.LocalDate;

public record TrainerCriteriaDto (Long id, String traineeName, LocalDate startDate, LocalDate endDate) {
}
