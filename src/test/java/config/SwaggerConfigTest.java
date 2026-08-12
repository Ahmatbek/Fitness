package config;

import io.swagger.v3.oas.models.OpenAPI;
import kg.biamino.projects.config.SwaggerConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SwaggerConfigTest {

    @Test
    void customOpenApi_returnsOpenApiWithTitle() {
        OpenAPI openAPI = new SwaggerConfig().customOpenApi();

        assertNotNull(openAPI);
        assertEquals("Epam Training", openAPI.getInfo().getTitle());
    }
}
