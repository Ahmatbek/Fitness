package kg.biamino.projects.service;

import kg.biamino.projects.dto.TrainingTypeDto;
import kg.biamino.projects.model.TrainingType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TrainingTypeService {
    @Transactional(readOnly = true)
    TrainingType findById(Long id);

//    @Transactional
//    TrainingType save(TrainingType trainingType);

    @Transactional(readOnly = true)
    TrainingType findByName(String name);

    @Transactional(readOnly = true)
    List<TrainingTypeDto> findAll();
}
