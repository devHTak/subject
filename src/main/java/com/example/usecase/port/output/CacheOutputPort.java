package com.example.usecase.port.output;

import java.util.concurrent.TimeUnit;

public interface CacheOutputPort {
    void lockWithKey(String key, int expireTime, TimeUnit expireTimeUnit);

    void unlockWithKey(String key);

    String findLockValueWithKey(String key);
}
