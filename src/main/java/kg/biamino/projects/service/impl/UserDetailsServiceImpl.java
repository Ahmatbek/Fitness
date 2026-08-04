package kg.biamino.projects.service.impl;

import kg.biamino.projects.enums.Role;
import kg.biamino.projects.exception.UserInactiveException;
import kg.biamino.projects.model.AppUserDetails;
import kg.biamino.projects.model.User;
import kg.biamino.projects.repository.TraineeRepository;
import kg.biamino.projects.repository.TrainerRepository;
import kg.biamino.projects.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public UserDetailsServiceImpl(UserRepository userRepository,  TraineeRepository traineeRepository, TrainerRepository trainerRepository) {
        this.userRepository = userRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        if(user.getIsActive().equals(Boolean.FALSE)) {
            throw new UserInactiveException("user is inactive");
        }
        String role=null;
        if(traineeRepository.findTraineeByUserId(user.getId()).isPresent()){
            role= Role.TRAINEE.name().toUpperCase();
        }
        else if(trainerRepository.findByUserId(user.getId()).isPresent()){
            role= Role.TRAINER.name().toUpperCase();
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));

        return new AppUserDetails(user.getUsername(),
                                    user.getPassword(),
                                    authorities);

    }
}
