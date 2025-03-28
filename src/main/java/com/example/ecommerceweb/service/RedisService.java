package com.example.ecommerceweb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String TOKEN_BLACKLIST_PREFIX = "blacklist:token:";
    private static final String ACCESS_TOKEN_PREFIX = "access:token:";

    public void saveRevokedToken(String token, long ttl) {
        String key = TOKEN_BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "revoked", ttl, TimeUnit.SECONDS);
    }

    public boolean isTokenRevoked(String token) {
        String key = TOKEN_BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void saveAccessToken(String token, String userId, long ttl) {
        String key = ACCESS_TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, userId, ttl, TimeUnit.SECONDS);
    }

    public String getAccessTokenUserId(String token) {
        String key = ACCESS_TOKEN_PREFIX + token;
        return redisTemplate.opsForValue().get(key);
    }

    public void removeAccessToken(String token) {
        String key = ACCESS_TOKEN_PREFIX + token;
        redisTemplate.delete(key);
    }
} 