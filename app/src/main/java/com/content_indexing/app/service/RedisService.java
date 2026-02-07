package com.content_indexing.app.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper;
    public void set(String key, Object value, long ttlSeconds){
        try{
            redisTemplate.opsForValue().set(key,objectMapper.writeValueAsString(value), Duration.ofSeconds(ttlSeconds));
        }
        catch(Exception e){

        }

    }
    public <T> T get(String key, Class<T> clazz) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> void setList(String key, List<T> value, long ttlSeconds) {
        set(key, value, ttlSeconds);
    }

    public <T> List<T> getList(String key, Class<T> clazz) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<List<T>>() {});
        } catch (Exception e) {
            return null;
        }
    }

    public long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public long getLong(String key) {
        String val = redisTemplate.opsForValue().get(key);
        return val != null ? Long.parseLong(val) : 0;
    }

    public void setDouble(String key, double value) {
        redisTemplate.opsForValue().set(key, String.valueOf(value));
    }

    public double getDouble(String key) {
        String val = redisTemplate.opsForValue().get(key);
        return val != null ? Double.parseDouble(val) : 0.0;
    }

    public void zadd(String key, double score, String member) {
        redisTemplate.opsForZSet().add(key, member, score);
    }
}