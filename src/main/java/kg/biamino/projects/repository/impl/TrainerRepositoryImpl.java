package kg.biamino.projects.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kg.biamino.projects.model.Trainer;
import kg.biamino.projects.repository.TrainerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainer save(Trainer trainer){
        if(trainer == null){
            entityManager.persist(trainer);
        }else {
           trainer = entityManager.merge(trainer);
        }
        return trainer;
    }

    @Override
    public Optional<Trainer> findByUserId(Long id) {
        return Optional.ofNullable(
                entityManager.createQuery("from Trainer t where t.user.id=:userId", Trainer.class)
                        .setParameter("userId", id)
                        .getSingleResult());
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Trainer.class, id));
    }

    @Override
    public List<Trainer> findAll() {
        return entityManager.createQuery("from Trainer", Trainer.class).getResultList();
    }

    @Override
    public Trainer update(Trainer trainer){
        return entityManager.merge(trainer);
    }

    @Override
    public List<Trainer> findNotAssignedTrainees(Long traineeId){
        return entityManager.createQuery("""
        SELECT tr FROM Trainer tr
        where not exists (
            select 1 from Trainee t
            join Trainer trainer
            where t.id = :traineeId and
            trainer=tr
            
        )
""", Trainer.class)
                .setParameter("traineeId", traineeId)
                .getResultList();

    }

    }





