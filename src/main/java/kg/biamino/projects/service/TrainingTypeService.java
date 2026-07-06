package kg.biamino.projects.service;

import kg.biamino.projects.model.TrainingType;
import org.springframework.transaction.annotation.Transactional;

public interface TrainingTypeService {
    @Transactional(readOnly = true)
    TrainingType findById(Long id);

//    @Transactional
//    TrainingType save(TrainingType trainingType);

//    @Transactional
    TrainingType findByName(String name);
}
