package com.example.usecase.usecase.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheLock {
    String key();
    int expireTime() default 5;
    TimeUnit expireTimeUnit() default TimeUnit.SECONDS;
}
