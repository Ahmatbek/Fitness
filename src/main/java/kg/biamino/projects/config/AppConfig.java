package kg.biamino.projects.config;

import org.springframework.context.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


@Configuration
@ComponentScan( basePackages = "kg.biamino.projects",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ControllerConfig.class)
        }
)

@PropertySource("classpath:application.properties")
public class AppConfig {

        @Bean
        public LocalValidatorFactoryBean validator() {
                return new LocalValidatorFactoryBean();
        }



}
