package kg.biamino.projects.config;

import kg.biamino.projects.model.User;
import kg.biamino.projects.repository.UserRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class UserHealthIndicator implements HealthIndicator {
    private final UserRepository userRepository;

    public UserHealthIndicator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Health health(){
        boolean indicator = userExistsInDB();
        if (indicator) {
            return Health.up()
                    .withDetail("user", "users exist")
                    .build();
        }
        else {
            return Health.down()
                    .withDetail("user", "users not exist")
                    .build();
        }
    }

    private boolean userExistsInDB(){
        User user = userRepository.findAll().stream().findFirst().orElse(null);
        return user != null;

    }
}
