package com.example.framework.output.adapter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CacheAdapterTest {

    @Autowired
    private CacheAdapter cacheAdapter;

    @Test
    @DisplayName("5초 만료 테스트")
    void lock_expire_5_seconds_test() throws Exception{
        String key = "TEST";
        long expireTime = 5000L;
        cacheAdapter.lockWithKey(key, (int)expireTime, TimeUnit.MILLISECONDS);

        assertEquals(key, cacheAdapter.findLockValueWithKey(key));
        Thread.sleep(expireTime);
        assertNull(cacheAdapter.findLockValueWithKey(key));
    }

    @Test
    @DisplayName("lock 후 unlock 테스트")
    void lock_and_unlock_test() {
        String key = "TEST";
        cacheAdapter.lockWithKey(key, 5, TimeUnit.SECONDS);

        assertEquals(key, cacheAdapter.findLockValueWithKey(key));
        cacheAdapter.unlockWithKey(key);
        assertNull(cacheAdapter.findLockValueWithKey(key));
    }

}