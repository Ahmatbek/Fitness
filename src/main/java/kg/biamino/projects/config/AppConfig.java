package kg.biamino.projects.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@ComponentScan("kg.biamino.projects")
@PropertySource("classpath:application.properties")
public class AppConfig {



}
