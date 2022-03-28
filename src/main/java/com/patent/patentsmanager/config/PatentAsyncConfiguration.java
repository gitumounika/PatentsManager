package com.patent.patentsmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * author:mounika
 * Async Thread Pool Configuration
 */
@Configuration
@EnableAsync
public class PatentAsyncConfiguration {
    @Bean(name = "asyncTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setTaskDecorator(new PatentTaskDecorator());
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setKeepAliveSeconds(10);
        executor.setThreadNamePrefix("Thread Pool-");
        executor.initialize();
        return executor;
    }
}


