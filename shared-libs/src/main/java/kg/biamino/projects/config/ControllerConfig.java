package kg.biamino.projects.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.micrometer.common.lang.NonNull;

@Configuration
public class ControllerConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(handlerInterceptor());
    }

    private TransactionLoggingInterceptor handlerInterceptor() {
        return new TransactionLoggingInterceptor();

    }



}
