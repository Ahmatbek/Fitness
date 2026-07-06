package kg.biamino.projects.service.impl;

import kg.biamino.projects.model.TrainingType;
import kg.biamino.projects.repository.TrainingTypeRepository;
import kg.biamino.projects.service.TrainingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {
    private  TrainingTypeRepository trainingTypeRepository;

    @Autowired
    public void setTrainingTypeRepository(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public TrainingType findById(Long id) {
        return trainingTypeRepository.findById(id).orElseThrow(()-> new NoSuchElementException("no training type found with id: " + id));
    }

//    @Transactional
//    @Override
//    public TrainingType save(TrainingType trainingType) {
//        return trainingTypeRepository.save(trainingType);
//    }

//    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    @Override
    public TrainingType findByName(String name) {
        return trainingTypeRepository.findByName(name).orElseThrow(()-> new NoSuchElementException("no training type found with name: " + name));
    }

}
