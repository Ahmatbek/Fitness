package kg.biamino.projects.dao.impl;

import kg.biamino.projects.dao.UserDao;
import kg.biamino.projects.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class UserDaoImpl implements UserDao {

    private Map<String, User> userMap;

    @Autowired
    public void setUserMap(Map<String, User> userMap) {
        this.userMap = userMap;
    }

    @Override
    public User getUserByUsername(String username){
        return userMap.get(username);
    }

    @Override
    public List<User> getAllUsers(){
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User createUser(User user){
      userMap.put(user.getUsername(), user);
      return user;
    }

    @Override
    public User updateUser(User user){
        userMap.put(user.getUsername(), user);
        return user;
    }

    @Override
    public void deleteUser(String username){
        userMap.remove(username);
    }





}
