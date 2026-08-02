package config;

import kg.biamino.projects.config.ControllerConfig;
import kg.biamino.projects.config.TransactionLoggingInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ControllerConfigTest {

    @Test
    void addInterceptors_registersTransactionLoggingInterceptor() {
        ControllerConfig controllerConfig = new ControllerConfig();
        InterceptorRegistry registry = mock(InterceptorRegistry.class);

        controllerConfig.addInterceptors(registry);

        verify(registry).addInterceptor(any(TransactionLoggingInterceptor.class));
    }
}
