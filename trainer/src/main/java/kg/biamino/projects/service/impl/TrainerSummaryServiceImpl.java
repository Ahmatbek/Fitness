package kg.biamino.projects.service.impl;

import kg.biamino.projects.enums.ActionType;
import kg.biamino.projects.dto.TrainerWorkloadRequest;
import kg.biamino.projects.dto.MonthDto;
import kg.biamino.projects.dto.MonthlyDurationDto;
import kg.biamino.projects.dto.TrainerSummaryResponse;
import kg.biamino.projects.dto.YearsDto;
import kg.biamino.projects.exception.TrainerNotFoundException;
import kg.biamino.projects.model.TrainerSummary;
import kg.biamino.projects.repository.TrainerSummaryRepository;
import kg.biamino.projects.service.TrainerSummaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TrainerSummaryServiceImpl implements TrainerSummaryService {

    private final TrainerSummaryRepository trainingRepository;

    public TrainerSummaryServiceImpl(TrainerSummaryRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Override
    @JmsListener(destination = "training-queue", containerFactory = "jmsListenerContainerFactory")
    public void updateTrainerWorkload(TrainerWorkloadRequest trainerWorkloadRequest) throws IllegalArgumentException {
        validatingIncomingDto(trainerWorkloadRequest);
        if (trainerWorkloadRequest.getActionType().equals(ActionType.ADD)) {
            TrainerSummary newTrainerSummary = getTrainerSummary(trainerWorkloadRequest);
            log.info("Saving adding trainer's workload {}", newTrainerSummary);
            trainingRepository.save(newTrainerSummary);

        } else if (trainerWorkloadRequest.getActionType().equals(ActionType.DELETE)) {
            TrainerSummary newTrainerSummary = getTrainerSummary(trainerWorkloadRequest);
            newTrainerSummary.setDuration(-trainerWorkloadRequest.getTrainingDuration());
            log.info("Saving deletion trainer's workload {}", newTrainerSummary);
            trainingRepository.save(newTrainerSummary);
        }
    }

    private static TrainerSummary getTrainerSummary(TrainerWorkloadRequest trainerWorkloadRequest) {
        TrainerSummary newTrainerSummary = new TrainerSummary();
        newTrainerSummary.setUsername(trainerWorkloadRequest.getTrainerUsername());
        newTrainerSummary.setFirstName(trainerWorkloadRequest.getTrainerFirstName());
        newTrainerSummary.setLastName(trainerWorkloadRequest.getTrainerLastName());
        newTrainerSummary.setActive(trainerWorkloadRequest.isActive());
        newTrainerSummary.setTrainingDate(trainerWorkloadRequest.getTrainingDate());
        newTrainerSummary.setDuration(trainerWorkloadRequest.getTrainingDuration());
        return newTrainerSummary;
    }

    @Override
    public TrainerSummaryResponse getMonthlySummaryByTrainerUsername(String username) {
        TrainerSummary trainerSummary = trainingRepository.findByUsername(username)
                .stream()
                .findFirst()
                .orElseThrow(()-> new TrainerNotFoundException("Trainer not found"));
        log.info("Getting monthly summary for trainer {}", username);
        List<MonthlyDurationDto> summary = trainingRepository.getMonthlySummary(username);
        Map<Integer, List<MonthlyDurationDto>> map = summary
                .stream()
                .collect(Collectors.groupingBy(MonthlyDurationDto::getYear));

       List<YearsDto> years =  map.entrySet()
               .stream()
               .map(entry-> YearsDto.builder()
                       .year(entry.getKey())
                       .monthDtoList(entry.getValue().stream()
                               .filter(e-> e.getTotalDuration() != 0)
                                      .map(e-> new MonthDto(e.getMonth(), e.getTotalDuration()))
                                      .toList())
                              .build())
               .filter(yearDto -> !yearDto.getMonthDtoList().isEmpty())
               .toList();

        TrainerSummaryResponse trainerSummaryResponse = new TrainerSummaryResponse();
        trainerSummaryResponse.setFirstName(trainerSummary.getFirstName());
        trainerSummaryResponse.setLastName(trainerSummary.getLastName());
        trainerSummaryResponse.setUsername(username);
        trainerSummaryResponse.setStatus(trainerSummary.getActive());
        trainerSummaryResponse.setYearsDtoList(years);

        log.info("Returning monthly summary for trainer {}", username);

        return trainerSummaryResponse;
    }

    private void validatingIncomingDto(TrainerWorkloadRequest trainerWorkloadRequest) throws IllegalArgumentException {
        if (trainerWorkloadRequest.getTrainerUsername() == null) throw new IllegalArgumentException("Trainer username is null");
        if(trainerWorkloadRequest.getTrainingDate() == null) throw new IllegalArgumentException("Training date is null");
        if(trainerWorkloadRequest.getActionType()==null) throw new IllegalArgumentException("Action type is null");
        if(trainerWorkloadRequest.getTrainerFirstName()==null) throw new IllegalArgumentException("Trainer first name is null");
        if(trainerWorkloadRequest.getTrainerLastName()==null) throw new IllegalArgumentException("Trainer last name is null");
        if(trainerWorkloadRequest.getTrainingDuration()<=0) throw new IllegalArgumentException("Training duration is null");
        if(trainerWorkloadRequest.getTrainingDate().isBefore(LocalDate.now())) throw new IllegalArgumentException("Training date is in the past");

    }
}
