package config;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import kg.biamino.projects.config.MetricsConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MetricsConfigTest {

    private final MetricsConfig metricsConfig = new MetricsConfig();
    private final SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();

    @Test
    void counterAspect_returnsCountedAspect() {
        CountedAspect countedAspect = metricsConfig.counterAspect(meterRegistry);

        assertNotNull(countedAspect);
    }

    @Test
    void timedAspect_returnsTimedAspect() {
        TimedAspect timedAspect = metricsConfig.timedAspect(meterRegistry);

        assertNotNull(timedAspect);
    }
}
