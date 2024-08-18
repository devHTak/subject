package com.example.framework.output.adapter;

import com.example.usecase.port.output.CacheOutputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheAdapter implements CacheOutputPort {
    private final Logger log = LoggerFactory.getLogger(CacheAdapter.class);

    private final RedisTemplate redisTemplate;

    @Autowired
    public CacheAdapter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void lockWithKey(String key, int expireTime, TimeUnit expireTimeUnit) {
        log.info("LOCK With Key({}), expire({})", key, expireTime);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, key);

        redisTemplate.expire(key, expireTime, expireTimeUnit);
    }

    @Override
    public String findLockValueWithKey(String key) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        return (String) valueOperations.get(key);
    }

    @Override
    public void unlockWithKey(String key) {
        log.info("UNLOCK With Key({})", key);
        redisTemplate.delete(key);
    }
}
