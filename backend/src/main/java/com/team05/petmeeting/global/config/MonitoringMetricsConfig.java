package com.team05.petmeeting.global.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitoringMetricsConfig {
    public MonitoringMetricsConfig(MeterRegistry meterRegistry) {
        meterRegistry.config().commonTags("application", "pet-meeting");
    }
}
