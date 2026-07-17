package kg.biamino.projects.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import kg.biamino.projects.dto.TraineeTrainingsDto;
import kg.biamino.projects.dto.TrainerTrainingsDto;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.records.TraineeCriteriaDto;
import kg.biamino.projects.records.TrainerCriteriaDto;
import kg.biamino.projects.repository.TrainingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingRepositoryImpl implements TrainingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Training save(Training training){
        if(training.getId() == null){
            entityManager.persist(training);
        }else{
            training = entityManager.merge(training);
        }
        return training;

    }

    @Override
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Training.class, id));
    }

    @Override
    public List<Training> findAll() {
        return entityManager.createQuery("from Training", Training.class).getResultList();
    }


    @Override
    public List<Training> findByCriteria(String username, TraineeTrainingsDto criteria) {
        StringBuilder jpql = new StringBuilder("""
        SELECT t FROM Training t
        WHERE t.trainee.user.username = :username
        """);

        if (criteria.getFrom() != null) {
            jpql.append(" AND t.date >= :startDate");
        }
        if (criteria.getTo() != null) {
            jpql.append(" AND t.date <= :endDate");
        }
        if (criteria.getTrainerName() != null) {
            jpql.append(" AND t.trainer.user.firstName = :trainerFirstName");
        }
        if (criteria.getTrainingType() != null) {
            jpql.append(" AND t.trainingType.name = :trainingTypeName");
        }

        TypedQuery<Training> query = entityManager.createQuery(jpql.toString(), Training.class);
        query.setParameter("username", username);

        if (criteria.getFrom() != null) {
            query.setParameter("startDate", criteria.getFrom());
        }
        if (criteria.getTo() != null) {
            query.setParameter("endDate", criteria.getTo());
        }
        if (criteria.getTrainerName() != null) {
            query.setParameter("trainerFirstName", criteria.getTrainerName());
        }
        if (criteria.getTrainingType() != null) {
            query.setParameter("trainingTypeName", criteria.getTrainingType());
        }

        return query.getResultList();
    }

    @Override
    public List<Training> findByCriteria(String  username, TrainerTrainingsDto trainerTrainingsDto) {

        StringBuilder jpql = new StringBuilder("""
                SELECT t FROM Training t
                WHERE t.trainer.user.username = :username
                """);
        if(trainerTrainingsDto.getFrom() != null) {
            jpql.append(" AND t.date >= :startDate");
        }
        if(trainerTrainingsDto.getTo() != null) {
            jpql.append(" AND t.date <= :endDate");
        }
        if(trainerTrainingsDto.getTraineeName() != null) {
            jpql.append(" AND t.trainee.user.firstName = :traineeFirstName");
        }
        TypedQuery<Training> query = entityManager.createQuery(jpql.toString(), Training.class);
        query.setParameter("username", username);
        if(trainerTrainingsDto.getFrom() != null) {
            query.setParameter("startDate", trainerTrainingsDto.getFrom());
        }
        if(trainerTrainingsDto.getTo() != null) {
            query.setParameter("endDate", trainerTrainingsDto.getTo());
        }
        if(trainerTrainingsDto.getTraineeName() != null) {
            query.setParameter("traineeFirstName", trainerTrainingsDto.getTraineeName());
        }
        return query.getResultList();



    }
}
