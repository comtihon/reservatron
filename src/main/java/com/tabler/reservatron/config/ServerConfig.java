package com.tabler.reservatron.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

@Configuration
public class ServerConfig {
    @Value("${lock.prefix}")
    private String registryKey;

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Bean
    public LockRegistry lockRegistry() {
        return new RedisLockRegistry(connectionFactory, registryKey);
    }
}
