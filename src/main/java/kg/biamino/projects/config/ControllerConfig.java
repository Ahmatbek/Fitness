package kg.biamino.projects.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(
        basePackages = "kg.biamino.projects",
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value=Controller.class),
        useDefaultFilters = false)
public class ControllerConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(handlerInterceptor());
    }

    private TransactionLoggingInterceptor handlerInterceptor() {
        return new TransactionLoggingInterceptor();

    }



}
