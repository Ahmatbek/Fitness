
package cucumber.config;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;

import io.cucumber.spring.CucumberContextConfiguration;

@SpringBootTest(classes={kg.biamino.projects.Main.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.activemq.broker-url=vm://localhost?broker.persistent=false"
})
@CucumberContextConfiguration
@ActiveProfiles("test")
public class CucumberSpringConfig {

    private static final MongoDBContainer MONGO_CONTAINER = new MongoDBContainer("mongo:7.0");

    static {
        MONGO_CONTAINER.start();
    }

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGO_CONTAINER::getReplicaSetUrl);
    }

}

