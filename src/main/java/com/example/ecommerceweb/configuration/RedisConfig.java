package com.example.ecommerceweb.configuration;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;

import java.time.Duration;

@Slf4j
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.redis.lettuce.pool.max-active:8}")
    private int maxActive;

    @Value("${spring.data.redis.lettuce.pool.max-idle:8}")
    private int maxIdle;

    @Value("${spring.data.redis.lettuce.pool.min-idle:0}")
    private int minIdle;

    @Value("${spring.data.redis.lettuce.pool.max-wait:-1}")
    private long maxWait;

    @Value("${spring.data.redis.timeout:2000}")
    private long timeout;
    
    @Value("${spring.data.redis.connect-timeout:2000}")
    private long connectTimeout;
    
    @Value("${spring.data.redis.retry.max-attempts:3}")
    private int maxRetryAttempts;
    
    @Value("${spring.data.redis.retry.delay:1000}")
    private long retryDelay;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        try {
            log.info("Configuring Redis connection factory with host: {}, port: {}", redisHost, redisPort);
            
            RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
            if (redisPassword != null && !redisPassword.isEmpty()) {
                redisConfig.setPassword(redisPassword);
            }

            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            poolConfig.setMaxTotal(maxActive);
            poolConfig.setMaxIdle(maxIdle);
            poolConfig.setMinIdle(minIdle);
            poolConfig.setMaxWaitMillis(maxWait);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
            poolConfig.setTestWhileIdle(true);
            // Add JMX support for monitoring
            poolConfig.setJmxEnabled(true);

            // Configure socket options with connection timeout and keep-alive
            SocketOptions socketOptions = SocketOptions.builder()
                    .connectTimeout(Duration.ofMillis(connectTimeout))
                    .keepAlive(true)
                    .build();

            // Configure client options with disconnect tolerance
            ClientOptions clientOptions = ClientOptions.builder()
                    .socketOptions(socketOptions)
                    .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                    .autoReconnect(true)
                    .build();

            // Build client configuration with retry options
            LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                    .commandTimeout(Duration.ofMillis(timeout))
                    .poolConfig(poolConfig)
                    .clientOptions(clientOptions)
                    .build();

            LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig, clientConfig);
            factory.setValidateConnection(true);  // Validate connection before use
            factory.afterPropertiesSet();
            
            log.info("Redis connection factory configured successfully");
            return factory;
        } catch (Exception e) {
            log.error("Failed to create Redis connection factory: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Redis connection", e);
        }
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        try {
            log.info("Configuring Redis template");
            RedisTemplate<String, String> template = new RedisTemplate<>();
            template.setConnectionFactory(connectionFactory);
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new StringRedisSerializer());
            template.setHashKeySerializer(new StringRedisSerializer());
            template.setHashValueSerializer(new StringRedisSerializer());
            template.setEnableTransactionSupport(true);
            template.afterPropertiesSet();
            
            log.info("Redis template configured successfully");
            return template;
        } catch (Exception e) {
            log.error("Failed to create Redis template: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Redis template", e);
        }
    }
    
    @Bean
    @ConditionalOnProperty(name = "spring.redis.health-check.enabled", havingValue = "true", matchIfMissing = true)
    public RedisHealthIndicator redisHealthCheck() {
        return new RedisHealthIndicator();
    }
    
    /**
     * Inner class to handle Redis health checks
     */
    public class RedisHealthIndicator {
        
        public boolean isRedisAvailable(RedisTemplate<String, String> redisTemplate) {
            try {
                return redisTemplate.getConnectionFactory().getConnection().ping() != null;
            } catch (Exception e) {
                log.error("Redis health check failed: {}", e.getMessage());
                return false;
            }
        }
    }
}