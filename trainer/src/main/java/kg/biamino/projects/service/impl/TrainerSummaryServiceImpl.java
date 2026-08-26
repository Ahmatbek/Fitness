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
import org.springframework.stereotype.Service;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TrainerSummaryServiceImpl implements TrainerSummaryService {

    private final TrainerSummaryRepository trainingRepository;
    private final Validator validator;

    public TrainerSummaryServiceImpl(TrainerSummaryRepository trainingRepository, Validator validator) {
        this.trainingRepository = trainingRepository;
        this.validator = validator;
    }

    @Override
    @JmsListener(destination = "training-queue", containerFactory = "jmsListenerContainerFactory")
    public void updateTrainerWorkload(TrainerWorkloadRequest trainerWorkloadRequest) {
        Set<ConstraintViolation<TrainerWorkloadRequest>> violations = validator.validate(trainerWorkloadRequest);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(message);
        }
        if (ActionType.ADD.equals(trainerWorkloadRequest.getActionType())) {
            TrainerSummary newTrainerSummary = getTrainerSummary(trainerWorkloadRequest);
            log.info("Saving adding trainer's workload {}", newTrainerSummary);
            trainingRepository.save(newTrainerSummary);

        } else if (ActionType.DELETE.equals(trainerWorkloadRequest.getActionType())) {
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
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found"));
        log.info("Getting monthly summary for trainer {}", username);
        List<MonthlyDurationDto> summary = trainingRepository.getMonthlySummary(username);
        Map<Integer, List<MonthlyDurationDto>> map = summary
                .stream()
                .collect(Collectors.groupingBy(MonthlyDurationDto::getYear));

        List<YearsDto> years = map.entrySet()
                .stream()
                .map(entry -> YearsDto.builder()
                        .year(entry.getKey())
                        .monthDtoList(entry.getValue().stream()
                                .filter(e -> e.getTotalDuration() != 0)
                                .map(e -> new MonthDto(e.getMonth(), e.getTotalDuration()))
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

}
