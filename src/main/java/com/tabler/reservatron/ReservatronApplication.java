package com.tabler.reservatron;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@SpringBootApplication
public class ReservatronApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReservatronApplication.class, args);
    }

    @Bean
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int cores = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(cores * 2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Reserve-");
        executor.initialize();
        return executor;
    }
}
