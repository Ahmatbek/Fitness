package kg.biamino.projects.repository;

import kg.biamino.projects.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {
     @Query(value = """
               Select t.* from trainees t
               left join trainee_traineer tt on tt.trainee_id=t.id
               join users u on u.id = t.user_id
               where t.user_id = :id
""", nativeQuery = true)
     Optional<Trainee> findTraineeByUserId(Long id);

     Optional<Trainee> findTraineeByUserUsername(String username);


}
