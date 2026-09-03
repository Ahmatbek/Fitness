package cucumber.steps;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.web.server.LocalServerPort;
import io.restassured.response.Response;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

public class LoginSteps {
    
    @LocalServerPort   
    private int port;

    private String username;
    private String password;
    private Response response;

    @Given("the application is running")
    public void the_application_is_running() {
        assertTrue(port > 0);
    }

    @Given("a user with username {string} and password {string}")
    public void a_user_with_username_and_password(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Given("a registered trainee")
    public void a_registered_trainee() {
        Response registerResponse = given()
                .contentType("application/json")
                .body(Map.of(
                        "firstName", "Akhmat",
                        "lastName", "Tursunbaev",
                        "address", "Bishkek",
                        "dateOfBirth", "1998-05-20"))
                .when()
                .post("http://localhost:" + port + "/trainees");

        assertEquals(200, registerResponse.getStatusCode());
        this.username = registerResponse.jsonPath().getString("username");
        this.password = registerResponse.jsonPath().getString("password");
        assertNotNull(this.username);
        assertNotNull(this.password);
    }

    @Given("the wrong password is used instead")
    public void the_wrong_password_is_used_instead() {
        this.password = this.password + "-wrong";
    }

    @When("the user attempts to log in")
    public void the_user_attempts_to_log_in() {
         response = given()
                .contentType("application/json")
                .body(Map.of("username", username, "password", password))
                .when()
                .post("http://localhost:" + port + "/auth/login");
    }

    @Then("the response should contain a valid token")
    public void the_response_should_contain_a_valid_token() {
        assertEquals(200, response.getStatusCode());
        String token = response.jsonPath().getString("token");
        assertNotNull(token);
        assertEquals(3, token.split("\\.").length, "expected a JWT with header.payload.signature");
    }

    @Then("the response should indicate an authentication failure")
    public void the_response_should_indicate_an_authentication_failure() {
        assertEquals(401, response.getStatusCode());
    }

    @Then("the response should indicate a bad request")
    public void the_response_should_indicate_a_bad_request() {
        assertEquals(400, response.getStatusCode());
    }


}
