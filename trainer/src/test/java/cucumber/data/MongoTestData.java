package cucumber.data;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import kg.biamino.projects.model.Month;
import kg.biamino.projects.model.TrainerSummary;
import kg.biamino.projects.model.YearsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

public class MongoTestData {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() {
        TrainerSummary trainerSummary = new TrainerSummary();
        trainerSummary.setUsername("Akhmatbek.Tursunbaev");
        trainerSummary.setFirstName("Akhmatbek");
        trainerSummary.setLastName("Tursunbaev");
        trainerSummary.setStatus(true);
        trainerSummary.setYears(List.of(
                new YearsEntity(2026, List.of(new Month(10, 10))),
                new YearsEntity(2027, List.of(new Month(1, 15)))));
        mongoTemplate.save(trainerSummary);
    }

    @After
    public void clearDatabase() {
        mongoTemplate.getDb().drop();
    }
}
