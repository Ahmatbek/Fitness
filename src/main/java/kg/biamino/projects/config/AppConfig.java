package kg.biamino.projects.config;

import org.springframework.context.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


@Configuration
public class AppConfig {

        @Bean
        public LocalValidatorFactoryBean validator() {
                return new LocalValidatorFactoryBean();
        }



}
