package cucumber.steps;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import kg.biamino.projects.dto.TrainerWorkloadRequest;
import kg.biamino.projects.enums.ActionType;

public class ContractCheck {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private TrainerWorkloadRequest receivedRequest;

    @Autowired
    private JmsTemplate jmsTemplate;

    @When("a TrainerWorkloadRequest is created with trainerUsername {string} and actionType {string}")
    public void createTrainerWorkloadRequest(String trainerUsername, String actionType) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername(trainerUsername);
        request.setActionType(ActionType.valueOf(actionType));
        request.setActive(true);
        request.setTrainerFirstName("Bekzat");
        request.setTrainerLastName("Isakov");
        request.setTrainingDate(LocalDate.now().plusDays(1));
        request.setTrainingDuration(60);

        jmsTemplate.convertAndSend("training-queue", request, message -> {
            message.setStringProperty("transactionId", "test-transaction-id");
            return message;
        });
    }

    @Then("the consumer should receive a valid TrainerWorkloadRequest")
    public void verifyTrainerWorkloadRequest() {
        receivedRequest = (TrainerWorkloadRequest) jmsTemplate.receiveAndConvert("training-queue");

        assertNotNull(receivedRequest);
        assertNotNull(receivedRequest.getTrainerUsername());
        assertNotNull(receivedRequest.getActionType());
        assertTrue(receivedRequest.isActive());
        assertEquals("Bekzat", receivedRequest.getTrainerFirstName());
        assertEquals("Isakov", receivedRequest.getTrainerLastName());
        assertTrue(receivedRequest.getTrainingDate().isAfter(LocalDate.now()));
        assertTrue(receivedRequest.getTrainingDuration() > 0);
    }

    @Then("the incoming dto should match the {string} schema contract")
    public void verifyTrainerWorkloadRequestSchema(String schemaFile) throws Exception {
        assertNotNull(receivedRequest, "no TrainerWorkloadRequest has been received yet");
        String body = objectMapper.writeValueAsString(receivedRequest);
        assertThat(body, matchesJsonSchemaInClasspath(schemaFile));
    }
}
