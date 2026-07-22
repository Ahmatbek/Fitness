package kg.biamino.projects.service.impl;

import kg.biamino.projects.dto.TrainingTypeDto;
import kg.biamino.projects.exception.TrainingTypeNotFoundException;
import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.repository.TrainingTypeRepository;
import kg.biamino.projects.service.TrainingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {
    private TrainingTypeRepository trainingTypeRepository;

    @Autowired
    public void setTrainingTypeRepository(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public TrainingType findById(Long id) {
        return trainingTypeRepository.findById(id).orElseThrow(()-> new TrainingTypeNotFoundException("no training type found with id: " + id));
    }


    @Transactional(readOnly = true)
    @Override
    public TrainingType findByName(String name) {
        return trainingTypeRepository.findByName(name).orElseThrow(()-> new TrainingTypeNotFoundException("no training type found with name: " + name));
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrainingTypeDto> findAll() {
        return trainingTypeRepository.findAll().stream().map(e-> TrainingTypeDto.builder()
                .id(e.getId())
                .name(e.getName())
                .build())
                .toList();
    }

}
