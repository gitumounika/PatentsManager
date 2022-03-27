package com.patent.patentsmanager.config;

import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.COUNT_BASED;


@Configuration
public class PatentEndpointCircuitBreakerConfiguration {

    @Bean
    public CircuitBreakerConfigCustomizer endPointUsptoCircuitBreakerConfig() {
        return CircuitBreakerConfigCustomizer
                .of("endPointUsptoCircuitBreaker",
                        builder -> builder.slidingWindowSize(5)
                                .slidingWindowType(COUNT_BASED)
                                .waitDurationInOpenState(Duration.ofSeconds(5))
                                .permittedNumberOfCallsInHalfOpenState(10)
                                .minimumNumberOfCalls(4)
                                .failureRateThreshold(50.0f));
    }
}

