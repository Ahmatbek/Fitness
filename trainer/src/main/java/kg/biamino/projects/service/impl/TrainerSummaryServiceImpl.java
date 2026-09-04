package kg.biamino.projects.service.impl;


import kg.biamino.projects.dto.TrainerWorkloadRequest;
import kg.biamino.projects.dto.MonthDto;
import kg.biamino.projects.dto.TrainerSummaryResponse;
import kg.biamino.projects.dto.YearsDto;
import kg.biamino.projects.exception.TrainerNotFoundException;
import kg.biamino.projects.model.Month;
import kg.biamino.projects.model.TrainerSummary;
import kg.biamino.projects.model.YearsEntity;
import kg.biamino.projects.repository.TrainerSummaryRepository;
import kg.biamino.projects.service.TrainerSummaryService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
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
    public void updateTrainerWorkload(TrainerWorkloadRequest trainerWorkloadRequest, @Header("transactionId") String transactionId) {
        MDC.put("transactionId", transactionId);
        log.info("Received message from training-queue with transactionId: {}", transactionId);
        try{
             Set<ConstraintViolation<TrainerWorkloadRequest>> violations = validator.validate(trainerWorkloadRequest);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(message);
        }
        log.info("Received trainer workload request {}", trainerWorkloadRequest);
        Optional<TrainerSummary> trainerSummary = trainingRepository.findByUsername(trainerWorkloadRequest.getTrainerUsername());
        trainerSummary.ifPresent(e-> operateWithExistingTrainerSummary(e,trainerWorkloadRequest));
        if(trainerSummary.isEmpty()) createNewTrainerSummaryDocument(trainerWorkloadRequest);
        }finally {
            MDC.remove("transactionId");
            log.info("Finished processing trainer workload request with transactionId: {}", transactionId);
        }
    }

    private static TrainerSummary getTrainerSummary(@NonNull TrainerWorkloadRequest trainerWorkloadRequest) {
        TrainerSummary newTrainerSummary = new TrainerSummary();
        newTrainerSummary.setUsername(trainerWorkloadRequest.getTrainerUsername());
        newTrainerSummary.setFirstName(trainerWorkloadRequest.getTrainerFirstName());
        newTrainerSummary.setLastName(trainerWorkloadRequest.getTrainerLastName());
        newTrainerSummary.setStatus(trainerWorkloadRequest.isActive());
        newTrainerSummary.setYears(
                List.of(new YearsEntity(trainerWorkloadRequest.getTrainingDate().getYear(),
                        List.of(new Month(trainerWorkloadRequest.getTrainingDate().getMonth().getValue(), trainerWorkloadRequest.getTrainingDuration())))));
        return newTrainerSummary;
    }

    @Override
    public TrainerSummaryResponse getMonthlySummaryByTrainerUsername(String username) {
        TrainerSummary trainerSummary = trainingRepository.findByUsername(username)
                .stream()
                .findFirst()
                .orElseThrow(() -> new TrainerNotFoundException("Trainer not found"));
        log.info("Getting monthly summary for trainer {}", username);

        return mapper(trainerSummary);
    }

    private void createNewTrainerSummaryDocument(TrainerWorkloadRequest trainerWorkloadRequest) {
        trainingRepository.save(getTrainerSummary(trainerWorkloadRequest));
    }
    private void operateWithExistingTrainerSummary(TrainerSummary trainerSummary, TrainerWorkloadRequest trainerWorkloadRequest) {
        Integer year = trainerWorkloadRequest.getTrainingDate().getYear();
        Integer month = trainerWorkloadRequest.getTrainingDate().getMonth().getValue();
        int duration = trainerWorkloadRequest.getTrainingDuration();
        AtomicReference<Boolean> isExistingMonth= new AtomicReference<>(false);
        List<YearsEntity> years = trainerSummary.getYears();

        years.stream()
                .filter(e -> e.getYear().equals(year))
                .findFirst()
                .flatMap(e -> e.getMonths()
                        .stream()
                        .filter(k -> k.getMonth().equals(month))
                        .findFirst())
                .ifPresent(m -> deleteOrAddDuration(m, duration, trainerWorkloadRequest, isExistingMonth));

        boolean yearFound = false;
        ListIterator<YearsEntity> iterator = years.listIterator();
        while (Boolean.FALSE.equals(isExistingMonth.get()) && iterator.hasNext()) {
            YearsEntity y = iterator.next();
            if (year.equals(y.getYear())) {
                yearFound = true;
               y.getMonths().add(new Month(month, duration));
                break; 
            }
        }
        if (Boolean.FALSE.equals(isExistingMonth.get()) && !yearFound) {
            years.add(new YearsEntity(year, new ArrayList<>(List.of(new Month(month, duration)))));
        }

        trainerSummary.setYears(years);
        trainingRepository.save(trainerSummary);

    }

    private TrainerSummaryResponse mapper(TrainerSummary trainerSummary){
        TrainerSummaryResponse trainerSummaryResponse = new TrainerSummaryResponse();
        trainerSummaryResponse.setFirstName(trainerSummary.getFirstName());
        trainerSummaryResponse.setLastName(trainerSummary.getLastName());
        trainerSummaryResponse.setUsername(trainerSummary.getUsername());
        trainerSummaryResponse.setStatus(trainerSummary.getStatus());
        trainerSummaryResponse.setYearsDtoList(trainerSummary.getYears().stream().map(this::mapper).toList());

        return trainerSummaryResponse;
    }
    private YearsDto mapper(YearsEntity years){
        YearsDto yearsDto = new YearsDto();
        yearsDto.setYear(years.getYear());
        yearsDto.setMonthDtoList(years.getMonths().stream()
                .map(e-> mapper(e, e.getDuration())).toList());
        return yearsDto;
    }
    private MonthDto mapper(Month month, int duration){
        MonthDto monthDto = new MonthDto();
        monthDto.setMonth(month.getMonth());
        monthDto.setTrainingSummaryDuration(duration);
        return monthDto;
    }

    private void deleteOrAddDuration(Month m, int duration, TrainerWorkloadRequest trainerWorkloadRequest, AtomicReference<Boolean> isExistingMonth){
        switch (trainerWorkloadRequest.getActionType()) {
            case DELETE -> m.setDuration(Math.max((m.getDuration() - duration), 0));
            case ADD -> m.setDuration(m.getDuration() + duration);
        }
        isExistingMonth.set(true);
    }


}
