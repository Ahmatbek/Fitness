package cucumber;

import cucumber.env.TestEnvironment;
import io.cucumber.java.BeforeAll;

public class IntegrationLifecycle {

    @BeforeAll
    public static void startEnvironment() {
        TestEnvironment.startOnce();
    }
}
