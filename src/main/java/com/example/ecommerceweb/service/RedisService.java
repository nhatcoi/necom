package com.example.ecommerceweb.service;

import com.example.ecommerceweb.configuration.RedisConfig.RedisHealthIndicator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    
    @Autowired(required = false)
    private RedisHealthIndicator redisHealthIndicator;
    
    @Value("${spring.data.redis.retry.max-attempts:3}")
    private int maxRetryAttempts;
    
    private static final String TOKEN_BLACKLIST_PREFIX = "blacklist:token:";
    private static final String ACCESS_TOKEN_PREFIX = "access:token:";

    /**
     * Save revoked token with retry mechanism
     */
    @Retryable(
        value = {RedisConnectionFailureException.class, RedisSystemException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void saveRevokedToken(String token, long ttl) {
        try {
            String key = TOKEN_BLACKLIST_PREFIX + token;
            log.debug("Saving revoked token with key: {}", key);
            redisTemplate.opsForValue().set(key, "revoked", ttl, TimeUnit.SECONDS);
            log.debug("Successfully saved revoked token");
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.error("Redis connection error while saving revoked token: {}", e.getMessage());
            handleRedisError(e);
        } catch (Exception e) {
            log.error("Unexpected error while saving revoked token: {}", e.getMessage(), e);
        }
    }

    /**
     * Check if token is revoked with fallback
     */
    public boolean isTokenRevoked(String token) {
        try {
            String key = TOKEN_BLACKLIST_PREFIX + token;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.error("Redis connection error while checking revoked token: {}", e.getMessage());
            handleRedisError(e);
            // In case of Redis failure, default to non-revoked for auth continuity
            return false;
        } catch (Exception e) {
            log.error("Unexpected error while checking revoked token: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Save access token with retry mechanism
     */
    @Retryable(
        value = {RedisConnectionFailureException.class, RedisSystemException.class}, 
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void saveAccessToken(String token, String userId, long ttl) {
        try {
            String key = ACCESS_TOKEN_PREFIX + token;
            log.debug("Saving access token for user {} with key: {}", userId, key);
            redisTemplate.opsForValue().set(key, userId, ttl, TimeUnit.SECONDS);
            log.debug("Successfully saved access token");
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.error("Redis connection error while saving access token: {}", e.getMessage());
            handleRedisError(e);
        } catch (Exception e) {
            log.error("Unexpected error while saving access token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save access token", e);
        }
    }

    /**
     * Get access token user ID with fallback
     */
    public String getAccessTokenUserId(String token) {
        try {
            String key = ACCESS_TOKEN_PREFIX + token;
            return redisTemplate.opsForValue().get(key);
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.error("Redis connection error while getting access token user ID: {}", e.getMessage());
            handleRedisError(e);
            return null;
        } catch (Exception e) {
            log.error("Unexpected error while getting access token user ID: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Remove access token with retry mechanism
     */
    @Retryable(
        value = {RedisConnectionFailureException.class, RedisSystemException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void removeAccessToken(String token) {
        try {
            String key = ACCESS_TOKEN_PREFIX + token;
            log.debug("Removing access token with key: {}", key);
            redisTemplate.delete(key);
            log.debug("Successfully removed access token");
        } catch (RedisConnectionFailureException | RedisSystemException e) {
            log.error("Redis connection error while removing access token: {}", e.getMessage());
            handleRedisError(e);
        } catch (Exception e) {
            log.error("Unexpected error while removing access token: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Handle Redis errors and check connection health
     */
    private void handleRedisError(Exception e) {
        log.error("Redis operation failed: {}", e.getMessage());
        
        if (redisHealthIndicator != null) {
            boolean available = redisHealthIndicator.isRedisAvailable(redisTemplate);
            log.info("Redis health check after error: available = {}", available);
            
            if (!available) {
                log.warn("Redis server appears to be unavailable. Operations will continue with degraded functionality.");
            }
        }
    }
}