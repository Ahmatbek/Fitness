package kg.biamino.projects.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    public List<Training> findByCriteria(String username , TraineeCriteriaDto traineeCriteriaDto) {
        return entityManager.createQuery("""
        SELECT t from Training t 
         where t.trainee.user.username = :username
         and t.trainer.user.firstName = :trainerFirstName
          and t.trainingType.name=:trainingTypeName
          and t.date between :startDate and :endDate
""", Training.class)
                .setParameter("username", username)
                .setParameter("startDate", traineeCriteriaDto.startDate())
                .setParameter("endDate", traineeCriteriaDto.endDate())
                .setParameter("trainingTypeName", traineeCriteriaDto.trainingTypeName())
                .setParameter("trainerFirstName", traineeCriteriaDto.trainerFirstName())
                .getResultList();
    }


    @Override
    public List<Training> findByCriteria(String  username, TrainerCriteriaDto traineeCriteriaDto) {

        return entityManager.createQuery("""
        SELECT t from Training t 
        where t.trainer.user.username = :username
        and t.trainee.user.firstName = :traineeFirstName
        and t.date between :startDate and :endDate
""" ,Training.class)
                .setParameter("username", username)
                .setParameter("startDate", traineeCriteriaDto.startDate())
                .setParameter("endDate", traineeCriteriaDto.endDate())
                .setParameter("traineeFirstName", traineeCriteriaDto.traineeName())
                .getResultList();
    }
}
