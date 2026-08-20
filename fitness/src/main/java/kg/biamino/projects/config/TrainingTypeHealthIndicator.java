package kg.biamino.projects.config;

import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.repository.TrainingTypeRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypeHealthIndicator implements HealthIndicator {

    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeHealthIndicator(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }
    @Override
    public Health health() {
        boolean check = atLeastOneEntityExists();
        if (check) {
            return Health.up()
                    .withDetail("At least one record of trainingType exists", true)
                    .build();
        }
        return Health.down()
                .withDetail("Record of trainingType is not present", false)
                .build();
    }


    private boolean atLeastOneEntityExists(){
       TrainingType tr = trainingTypeRepository.findAll().stream().findFirst().orElse(null);
        return tr != null;
    }
}
