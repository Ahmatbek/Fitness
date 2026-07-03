package kg.biamino.projects.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import kg.biamino.projects.model.User;
import kg.biamino.projects.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User save(User user) {
        if(user.getId() == null) {
            entityManager.persist(user);
        }else{
          user = entityManager.merge(user);
        }
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Override
    public List<User> findAll() {
        return entityManager.createQuery("from User u", User.class).getResultList();
    }

    @Override
    public void deleteById(Long id){
        entityManager.remove(entityManager.find(User.class, id));
    }

    @Override
    public Optional<User> findUserByUsername(String username){
        try{
            User user =  entityManager.createQuery("SELECT u from User u where u.username=:username", User.class)
                    .setParameter("username",username)
                    .getSingleResult();
            return Optional.of(user);
        }catch(NoResultException e){
            return Optional.empty();
        }

    }

    @Override
    public User update(User user){
        return entityManager.merge(user);
    }


}
