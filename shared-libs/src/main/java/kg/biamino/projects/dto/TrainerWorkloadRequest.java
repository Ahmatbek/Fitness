package kg.biamino.projects.dto;


import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kg.biamino.projects.enums.ActionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
public class TrainerWorkloadRequest implements Serializable {
    @NotBlank(message = "trainer username can't be blank")
    private String trainerUsername;
    @NotBlank(message = "trainer first name can't be blank")
    private String trainerFirstName;
    @NotBlank(message = "trainer last name can't be blank")
    private String trainerLastName;
    private boolean isActive;
    @NotNull(message = "training date can't be null")
    @FutureOrPresent(message = "training date can't be in the past")
    private LocalDate trainingDate;
    @NotNull(message = "training duration can't be null")
    @Min(value = 1, message = "training duration can't be less than 1")
    private Integer trainingDuration;
    @NotNull(message = "action type can't be null")
    private ActionType actionType;
}