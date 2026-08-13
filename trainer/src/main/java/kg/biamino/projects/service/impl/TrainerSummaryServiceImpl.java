package kg.biamino.projects.service.impl;

import kg.biamino.projects.ActionType;
import kg.biamino.projects.TrainerWorkloadRequest;
import kg.biamino.projects.MonthDto;
import kg.biamino.projects.dto.MonthlyDurationDto;
import kg.biamino.projects.TrainerSummaryResponse;
import kg.biamino.projects.YearsDto;
import kg.biamino.projects.exception.TrainerNotFoundException;
import kg.biamino.projects.model.TrainerSummary;
import kg.biamino.projects.repository.TrainerSummaryRepository;
import kg.biamino.projects.service.TrainerSummaryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TrainerSummaryServiceImpl implements TrainerSummaryService {

    private final TrainerSummaryRepository trainingRepository;

    public TrainerSummaryServiceImpl(TrainerSummaryRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }


    @Override
    public void updateTrainerWorkload(TrainerWorkloadRequest trainerWorkloadRequest) {
        TrainerSummary trainerSummary = trainingRepository.findByUsernameAndTrainingDateAndDuration(trainerWorkloadRequest.getTrainerUsername(),
                        trainerWorkloadRequest.getTrainingDate(),
                        trainerWorkloadRequest.getTrainingDuration())
                .orElse(null);

        if (trainerWorkloadRequest.getActionType().equals(ActionType.ADD)) {
            TrainerSummary newTrainerSummary = getTrainerSummary(trainerWorkloadRequest);
            trainingRepository.save(newTrainerSummary);

        } else if (trainerSummary!=null && trainerWorkloadRequest.getActionType().equals(ActionType.DELETE)) {
            TrainerSummary newTrainerSummary = getTrainerSummary(trainerWorkloadRequest);
            newTrainerSummary.setDuration(-trainerWorkloadRequest.getTrainingDuration());
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
        TrainerSummary trainerSummary = trainingRepository.findByUsername(username).stream().findFirst().orElseThrow(()-> new TrainerNotFoundException("Trainer not found"));
        List<MonthlyDurationDto> summary = trainingRepository.getMonthlySummary(username);
        Map<Integer, List<MonthlyDurationDto>> map = summary
                .stream()
                .collect(Collectors.groupingBy(MonthlyDurationDto::getYear));

       List<YearsDto> years =  map.entrySet()
               .stream()
               .map(entry-> YearsDto.builder()
                       .year(entry.getKey())
                       .monthDtoList(entry.getValue().stream()
                                      .map(e-> new MonthDto(e.getMonth(), e.getTotalDuration()))
                                      .toList())
                              .build())
               .toList();

        TrainerSummaryResponse trainerSummaryResponse = new TrainerSummaryResponse();
        trainerSummaryResponse.setFirstName(trainerSummary.getFirstName());
        trainerSummaryResponse.setLastName(trainerSummary.getLastName());
        trainerSummaryResponse.setUsername(username);
        trainerSummaryResponse.setStatus(trainerSummary.getActive());
        trainerSummaryResponse.setYearsDtoList(years);
        return trainerSummaryResponse;

    }

}
