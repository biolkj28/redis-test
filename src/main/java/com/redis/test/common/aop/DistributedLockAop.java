package com.redis.test.common.aop;

import com.redis.test.common.exception.ExceptionIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {

    /**
     * Aop 동작 과정(트랜잭션 단위보다 크게 가져가야 동시성에 의한 데이터 이상현상이 발생하지 않음)
     * 1. 락 획득 이후-> 트랜잭션 시작 -> 트랜잭션 종료-> 락 해제
     * 락 해제 방법(코드 작성된 이외의 방법)
     * - TransactionalEventListener 의 AFTER_COMPLETION 사용
     * -
     */
    private static final String REDIS_LOCK_PREFIX = "RLOCK_";
    private final RedissonClient redissonClient;
    private final AopTransaction aopForTransaction;

    //Aop 로직 부분
    @Around("@annotation(com.redis.test.common.aop.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = REDIS_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());

        RLock rLock = redissonClient.getLock(key);

        try {
            log.info("try Lock Time {}, ThreadId {}", LocalDateTime.now(), Thread.currentThread().getId());

            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            if (!available) {
                throwException(distributedLock.exceptionClass(), distributedLock.exceptionMessage());
                return false;
            }

            log.info("get lock success {}" , key);
            return aopForTransaction.proceed(joinPoint);

        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            try {
                rLock.unlock();
            } catch (IllegalMonitorStateException e) {
                log.info("Redisson Lock Already UnLock {}",
                        e.getMessage()
                );
            }
        }
    }

    private void throwException(Class<? extends Exception> clazz, String message) throws Exception {
        Constructor<? extends Exception> constructor = clazz.getDeclaredConstructor(String.class);
        Exception exception = constructor.newInstance(message);

        if (exception instanceof ExceptionIgnore) {
            return;
        }

        throw exception;
    }

}
