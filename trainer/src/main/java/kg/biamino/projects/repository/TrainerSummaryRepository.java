package kg.biamino.projects.repository;

import kg.biamino.projects.dto.MonthlyDurationDto;
import kg.biamino.projects.model.TrainerSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerSummaryRepository extends JpaRepository<TrainerSummary, Long> {
    List<TrainerSummary> findByUsername(String username);

    Optional<TrainerSummary> findByUsernameAndTrainingDateAndDuration(String username, LocalDate date, int duration);

    @Query("""
    SELECT new kg.biamino.projects.dto.MonthlyDurationDto( YEAR(t.trainingDate) , MONTH(t.trainingDate),  SUM(t.duration))
    FROM TrainerSummary t
    WHERE t.username = :username
    GROUP BY YEAR(t.trainingDate), MONTH(t.trainingDate)
""")
    List<MonthlyDurationDto> getMonthlySummary(String username);
}
