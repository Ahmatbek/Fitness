package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kg.biamino.projects.config.JacksonConfig;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JacksonConfigTest {

    @Test
    void objectMapper_roundTripsJavaTimeTypes() throws Exception {
        ObjectMapper objectMapper = new JacksonConfig().objectMapper();

        assertNotNull(objectMapper);
        LocalDate date = LocalDate.of(2024, 1, 15);
        String json = objectMapper.writeValueAsString(date);
        LocalDate deserialized = objectMapper.readValue(json, LocalDate.class);

        assertEquals(date, deserialized);
    }
}
