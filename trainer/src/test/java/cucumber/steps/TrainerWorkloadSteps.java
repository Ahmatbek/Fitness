package cucumber.steps;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import kg.biamino.projects.dto.TrainerWorkloadRequest;
import kg.biamino.projects.enums.ActionType;
import kg.biamino.projects.model.TrainerSummary;
import kg.biamino.projects.repository.TrainerSummaryRepository;

public class TrainerWorkloadSteps {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private TrainerSummaryRepository trainerSummaryRepository;

    @When("an ADD workload request for trainer {string} with duration {int} for {int} month\\(s) from now is published")
    public void an_add_workload_request_is_published(String username, int duration, int monthsFromNow) {
        publish(username, ActionType.ADD, duration, LocalDate.now().plusMonths(monthsFromNow));
    }

    @When("a DELETE workload request for trainer {string} with duration {int} for {int} month\\(s) from now is published")
    public void a_delete_workload_request_is_published(String username, int duration, int monthsFromNow) {
        publish(username, ActionType.DELETE, duration, LocalDate.now().plusMonths(monthsFromNow));
    }

    @When("an invalid workload request with a blank trainer username is published")
    public void an_invalid_workload_request_is_published() {
        publish("", ActionType.ADD, 30, LocalDate.now().plusMonths(1));
    }

    private void publish(String username, ActionType actionType, int duration, LocalDate trainingDate) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername(username);
        request.setTrainerFirstName("Bekzat");
        request.setTrainerLastName("Isakov");
        request.setActive(true);
        request.setTrainingDate(trainingDate);
        request.setTrainingDuration(duration);
        request.setActionType(actionType);

        jmsTemplate.convertAndSend("training-queue", request, message -> {
            message.setStringProperty("transactionId", "test-" + System.nanoTime());
            return message;
        });
    }

    @Then("the trainer summary for {string} should eventually show {int} minutes for {int} month\\(s) from now")
    public void the_trainer_summary_should_eventually_show(String username, int expectedDuration, int monthsFromNow) {
        LocalDate target = LocalDate.now().plusMonths(monthsFromNow);
        int year = target.getYear();
        int month = target.getMonthValue();

        awaitCondition(10_000, () -> trainerSummaryRepository.findByUsername(username)
                .map(summary -> summary.getYears().stream().anyMatch(y -> y.getYear().equals(year)
                        && y.getMonths().stream().anyMatch(m -> m.getMonth().equals(month) && m.getDuration().equals(expectedDuration))))
                .orElse(false),
                () -> "expected " + expectedDuration + " minutes for " + username + " in " + year + "-" + month
                        + " but found: " + trainerSummaryRepository.findByUsername(username).orElse(null));
    }

    @Then("no trainer summary should ever be created for a blank username")
    public void no_trainer_summary_should_be_created() {
        sleep(2000);
        Optional<TrainerSummary> summary = trainerSummaryRepository.findByUsername("");
        assertTrue(summary.isEmpty(), () -> "expected no summary for a blank username but found: " + summary.get());
    }

    private interface Condition {
        boolean check();
    }

    private void awaitCondition(long timeoutMillis, Condition condition, java.util.function.Supplier<String> failureMessage) {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < deadline) {
            if (condition.check()) {
                return;
            }
            sleep(200);
        }
        fail(failureMessage.get());
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("interrupted while waiting");
        }
    }
}
