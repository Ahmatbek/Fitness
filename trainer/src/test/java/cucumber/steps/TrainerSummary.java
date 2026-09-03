package cucumber.steps;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.boot.test.web.server.LocalServerPort;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import kg.biamino.projects.dto.TrainerSummaryResponse;

public class TrainerSummary {

    @LocalServerPort
    private int port;

    private String authHeaderValue;
    private Response response;

    @Given("a valid JWT for an authenticated user")
    public void a_valid_jwt_for_an_authenticated_user() {
        authHeaderValue = "Bearer " + Hooks.jwtToken;
    }

    @Given("no Authorization header is provided")
    public void no_authorization_header_is_provided() {
        authHeaderValue = null;
    }

    @Given("an invalid JWT")
    public void an_invalid_jwt() {
        authHeaderValue = "Bearer invalid.token.value";
    }

    @When("the summary for trainer {string} is requested")
    public void the_summary_for_trainer_is_requested(String username) {
        var request = given()
                .accept("application/json")
                .header("Connection", "close");
        if (authHeaderValue != null) {
            request = request.header("Authorization", authHeaderValue);
        }
        response = request
                .when()
                .get("http://localhost:" + port + "/trainers/" + username + "/summary");
    }

    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @Then("the summary response should match the seeded workload for {string}")
    public void the_summary_response_should_match_the_seeded_workload_for(String username) {
        TrainerSummaryResponse summary = response.as(TrainerSummaryResponse.class);

        assertNotNull(summary);
        assertEquals(username, summary.getUsername());
        assertEquals("Akhmatbek", summary.getFirstName());
        assertEquals("Tursunbaev", summary.getLastName());
        assertEquals(true, summary.getStatus());
        assertEquals(2026, summary.getYearsDtoList().get(0).getYear());
        assertEquals(10, summary.getYearsDtoList().get(0).getMonthDtoList().get(0).getMonth());
        assertEquals(10, summary.getYearsDtoList().get(0).getMonthDtoList().get(0).getTrainingSummaryDuration());
        assertEquals(2027, summary.getYearsDtoList().get(1).getYear());
        assertEquals(1, summary.getYearsDtoList().get(1).getMonthDtoList().get(0).getMonth());
        assertEquals(15, summary.getYearsDtoList().get(1).getMonthDtoList().get(0).getTrainingSummaryDuration());
    }
}
