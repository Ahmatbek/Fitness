package cucumber.config;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import io.cucumber.spring.CucumberContextConfiguration;

@SpringBootTest(classes={kg.biamino.projects.Main.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.activemq.broker-url=vm://localhost?broker.persistent=false"
})
@CucumberContextConfiguration
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

}

