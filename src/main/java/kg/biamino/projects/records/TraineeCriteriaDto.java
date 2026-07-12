package kg.biamino.projects.records;

import java.time.LocalDate;

public record TraineeCriteriaDto (Long id, String trainerFirstName, String trainingTypeName, LocalDate startDate, LocalDate endDate) {}