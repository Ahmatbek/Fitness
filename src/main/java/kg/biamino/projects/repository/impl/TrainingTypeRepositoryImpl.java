package kg.biamino.projects.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kg.biamino.projects.model.Training;
import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.repository.TrainingTypeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public TrainingType save(TrainingType trainingType) {
        if(trainingType.getId() == null) {
            em.persist(trainingType);
        }else{
            em.merge(trainingType);
        }
        return trainingType;
    }
    @Override
    public Optional<TrainingType> findById(Long id) {
        return Optional.ofNullable(em.find(TrainingType.class, id));
    }

    @Override
    public Optional<TrainingType> findByName(String name) {
        return Optional.ofNullable(em.createQuery("from TrainingType t where t.name=:name", TrainingType.class)
                .setParameter("name", name)
                .getResultList().getFirst());
    }

    @Override
    public List<TrainingType> findAll(){
        return em.createQuery("from TrainingType", TrainingType.class).getResultList();
    }

}
