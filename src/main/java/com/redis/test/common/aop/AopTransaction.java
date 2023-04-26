package com.redis.test.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Component
public class AopTransaction {
    /**
     * 가장 중요
     *
     * 부모트랜잭션의 유무와 관계없이 동시성에 대한 처리는 별도의 트랜잭션으로 동작 해야함(따라서 requires_new)
     *
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
