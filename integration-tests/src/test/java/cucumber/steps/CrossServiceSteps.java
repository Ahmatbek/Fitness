package cucumber.steps;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.util.Map;

import cucumber.env.TestEnvironment;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import kg.biamino.projects.dto.TrainerSummaryResponse;

public class CrossServiceSteps {

    private String traineeUsername;
    private String trainerUsername;
    private String trainerJwt;
    private Response lastTrainingCreationResponse;
    private Long lastCreatedTrainingId;

    @Given("a registered trainee")
    public void a_registered_trainee() {
        Response response = given()
                .contentType("application/json")
                .body(Map.of(
                        "firstName", "Integration",
                        "lastName", "Trainee" + System.nanoTime(),
                        "address", "Bishkek",
                        "dateOfBirth", "1998-05-20"))
                .post(fitnessUrl("/trainees"));

        assertEquals(200, response.getStatusCode(), response.getBody().asString());
        traineeUsername = response.jsonPath().getString("username");
    }

    @Given("a registered trainer")
    public void a_registered_trainer() {
        Response response = given()
                .contentType("application/json")
                .body(Map.of(
                        "firstName", "Integration",
                        "lastName", "Trainer" + System.nanoTime(),
                        "specialization", "individual"))
                .post(fitnessUrl("/trainers"));

        assertEquals(200, response.getStatusCode(), response.getBody().asString());
        trainerUsername = response.jsonPath().getString("username");
        String trainerPassword = response.jsonPath().getString("password");

        Response login = given()
                .contentType("application/json")
                .body(Map.of("username", trainerUsername, "password", trainerPassword))
                .post(fitnessUrl("/auth/login"));

        assertEquals(200, login.getStatusCode(), login.getBody().asString());
        trainerJwt = login.jsonPath().getString("token");
    }

    @When("a training is created for {int} minutes {int} month\\(s) from now")
    public void a_training_is_created(int duration, int monthsFromNow) {
        lastTrainingCreationResponse = createTraining(traineeUsername, duration, monthsFromNow);
    }

    @When("a training is created for an unknown trainee for {int} minutes {int} month\\(s) from now")
    public void a_training_is_created_for_unknown_trainee(int duration, int monthsFromNow) {
        lastTrainingCreationResponse = createTraining("no-such-trainee-" + System.nanoTime(), duration, monthsFromNow);
    }

    @Given("a training was created for {int} minutes {int} month\\(s) from now")
    public void a_training_was_created(int duration, int monthsFromNow) {
        Response response = createTraining(traineeUsername, duration, monthsFromNow);
        assertEquals(200, response.getStatusCode(), response.getBody().asString());
        lastCreatedTrainingId = response.jsonPath().getLong("id");
    }

    @When("the training is deleted")
    public void the_training_is_deleted() {
        Response response = given()
                .header("Authorization", "Bearer " + trainerJwt)
                .delete(fitnessUrl("/trainings/" + lastCreatedTrainingId));
        assertEquals(204, response.getStatusCode(), response.getBody().asString());
    }

    private Response createTraining(String forTraineeUsername, int duration, int monthsFromNow) {
        return given()
                .header("Authorization", "Bearer " + trainerJwt)
                .contentType("application/json")
                .body(Map.of(
                        "traineeUsername", forTraineeUsername,
                        "trainerUsername", trainerUsername,
                        "trainingName", "Integration session",
                        "trainingType", "individual",
                        "trainingStart", LocalDate.now().plusMonths(monthsFromNow).toString(),
                        "duration", duration))
                .post(fitnessUrl("/trainings"));
    }

    @Then("the training creation should be rejected with status {int}")
    public void the_training_creation_should_be_rejected(int statusCode) {
        assertEquals(statusCode, lastTrainingCreationResponse.getStatusCode(),
                lastTrainingCreationResponse.getBody().asString());
    }

    @Then("the trainer's summary should eventually show {int} minutes for {int} month\\(s) from now")
    public void the_trainer_summary_should_eventually_show(int expectedDuration, int monthsFromNow) {
        LocalDate target = LocalDate.now().plusMonths(monthsFromNow);
        int year = target.getYear();
        int month = target.getMonthValue();

        long deadline = System.currentTimeMillis() + 20_000;
        TrainerSummaryResponse last = null;
        while (System.currentTimeMillis() < deadline) {
            Response response = given()
                    .header("Authorization", "Bearer " + trainerJwt)
                    .accept("application/json")
                    .get(trainerUrl("/trainers/" + trainerUsername + "/summary"));
            if (response.getStatusCode() == 200) {
                last = response.as(TrainerSummaryResponse.class);
                boolean matches = last.getYearsDtoList().stream().anyMatch(y -> y.getYear() == year
                        && y.getMonthDtoList().stream().anyMatch(m -> m.getMonth() == month && m.getTrainingSummaryDuration() == expectedDuration));
                if (matches) {
                    return;
                }
            }
            sleep(300);
        }
        fail("expected " + expectedDuration + " minutes in " + year + "-" + month + " but summary was: "
                + (last == null ? "never available" : last.getYearsDtoList()));
    }

    @Then("the trainer should have no workload summary")
    public void the_trainer_should_have_no_workload_summary() {
        sleep(3000);
        Response response = given()
                .header("Authorization", "Bearer " + trainerJwt)
                .accept("application/json")
                .get(trainerUrl("/trainers/" + trainerUsername + "/summary"));

        assertEquals(404, response.getStatusCode(), response.getBody().asString());
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String fitnessUrl(String path) {
        return "http://localhost:" + TestEnvironment.fitnessPort + path;
    }

    private String trainerUrl(String path) {
        return "http://localhost:" + TestEnvironment.trainerPort + path;
    }
}
