package kg.biamino.projects.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kg.biamino.projects.model.Training;
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
}
