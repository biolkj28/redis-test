package com.redis.test.common.aop;

import com.redis.test.common.exception.ExceptionIgnore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    //락 이름
    String key();

    //락 시간 단위
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    //락 획득 대기 시간
    long waitTime() default 5L;

    //락 점유 시간
    long leaseTime() default 3L;

//예외 클래스
 Class<? extends Exception>exceptionClass() default ExceptionIgnore.class;

    // 예외 메세지
    String exceptionMessage() default "";

}
