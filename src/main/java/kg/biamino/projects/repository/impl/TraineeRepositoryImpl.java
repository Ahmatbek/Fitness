package kg.biamino.projects.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kg.biamino.projects.model.Trainee;
import kg.biamino.projects.repository.TraineeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Trainee save(Trainee trainee) {
        entityManager.persist(trainee);
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        return entityManager.merge(trainee);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Trainee.class, id));
    }

    @Override
    public List<Trainee> findAll() {
        return entityManager.createQuery("from Trainee", Trainee.class).getResultList();
    }

    @Override
    public void deleteById(Long id){
        entityManager.remove(entityManager.find(Trainee.class, id));
    }

    @Override
    public Optional<Trainee> findByUsername(String user) {
        return Optional.ofNullable(entityManager.createQuery("select t from Trainee t where t.user.username=:user", Trainee.class)
                        .setParameter("user", user)
                .getSingleResult());
    }

    @Override
    public Optional<Trainee> findByUserId(Long id){
        return Optional.ofNullable(entityManager.createQuery("from Trainee t where t.user.id=:userId", Trainee.class).
                setParameter("userId", id)
                .getSingleResult());
    }




}
