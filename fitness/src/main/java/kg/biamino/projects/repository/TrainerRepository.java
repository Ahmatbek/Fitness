package kg.biamino.projects.repository;

import kg.biamino.projects.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUserId(Long id);

    Optional<Trainer> findByUserUsername(String username);

    @Query(value = """
SELECT tr.* 
FROM trainee_traineer tt
join trainers tr ON tt.trainer_id = tr.id
where tt.trainee_id != :traineeId 

""", nativeQuery = true)
    List<Trainer> findNotAssignedTrainees(Long traineeId);
}
