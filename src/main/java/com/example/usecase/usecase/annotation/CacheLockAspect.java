package com.example.usecase.usecase.annotation;

import com.example.usecase.exception.ExceptionCode;
import com.example.usecase.exception.PaymentBizException;
import com.example.usecase.port.output.CacheOutputPort;
import io.micrometer.common.util.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Aspect
@Component
public class CacheLockAspect {

    private final Logger log = LoggerFactory.getLogger(CacheLockAspect.class);

    private CacheOutputPort cacheOutputPort;

    @Autowired
    public CacheLockAspect(CacheOutputPort cacheOutputPort) {
        this.cacheOutputPort = cacheOutputPort;
    }

    @Before("@annotation(com.example.usecase.usecase.annotation.CacheLock)")
    public void before(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info("Cache Key Before: Method({})", signature.getMethod().getName());

        CacheLock cacheLock = signature.getMethod().getAnnotation(CacheLock.class);
        String key = this.getCachekey(joinPoint);

        String value = cacheOutputPort.findLockValueWithKey(key);
        if(StringUtils.isNotEmpty(value)) {
            throw new PaymentBizException(ExceptionCode.NOT_EXPIRE_TIME_REQUEST);
        }
        cacheOutputPort.lockWithKey(key, cacheLock.expireTime(), cacheLock.expireTimeUnit());
    }

    // 예외 발생 시 unlock 후 excpetion 제거
    @AfterThrowing(value = "@annotation(com.example.usecase.usecase.annotation.CacheLock)", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Exception e) throws Exception {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info("Cache Key AfterThrowing: Method({}), ExceptionMessage({})", signature.getMethod().getName(), e.getMessage());

        String key = this.getCachekey(joinPoint);
        cacheOutputPort.unlockWithKey(key);
        throw e;
    }

    private String getCachekey(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        CacheLock cacheLock = signature.getMethod().getAnnotation(CacheLock.class);

        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new StandardEvaluationContext();

        String[] parameterNames = signature.getParameterNames();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], joinPoint.getArgs()[i]);
        }

        Object key = Optional.ofNullable(parser.parseExpression(cacheLock.key()).getValue(context))
                .orElseThrow(() -> new PaymentBizException(ExceptionCode.NO_LOCK_KEY_IN_REQUEST));

        return key.toString();
    }

}
