package config;

import kg.biamino.projects.config.AppConfig;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppConfigTest {

    @Test
    void validator_returnsLocalValidatorFactoryBean() {
        AppConfig appConfig = new AppConfig();

        LocalValidatorFactoryBean validator = appConfig.validator();

        assertNotNull(validator);
    }
}
