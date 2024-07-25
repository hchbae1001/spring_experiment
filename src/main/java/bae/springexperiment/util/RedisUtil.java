package bae.springexperiment.util;

import bae.springexperiment.error.CustomException;
import bae.springexperiment.error.ErrorCode;
import bae.springexperiment.member.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Transactional
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public String getRedisString(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void setRedisString(String key, String value, Long expirationTime) {
        redisTemplate.opsForValue().set(key, value, expirationTime, TimeUnit.SECONDS);
    }

    public boolean isCacheExists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public <T> T convertStringToObject(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.NOT_EXIST_INFO);
        }
    }

    public void setRedisObjectToString(String key, Object obj, Long expirationTime) {
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(obj), expirationTime, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.NOT_EXIST_INFO);
        }
    }

    public void deleteRedisCache(String key) {
        redisTemplate.delete(key);
    }

    public <T> T getRedisStringToObject(String key, Class<T> valueType) {
        String json = redisTemplate.opsForValue().get(key);
        return convertStringToObject(json, valueType);
    }
}
