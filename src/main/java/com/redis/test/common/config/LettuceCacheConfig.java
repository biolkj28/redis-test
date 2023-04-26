//package com.redis.test.common.config;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
//import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//import java.time.Duration;
//
//
//@Configuration
//@EnableCaching
//public class LettuceCacheConfig {
//
//    /**
//     * Lettuce: 스핀 락 형태 -> 지속적인 락 획득 시도로 인한 데드락 발생 가능, 따라서 키의 순회 횟수를 지속적으로 확인하여 카운트 하여 확인 할 수 있지만,
//     * 결국 순회마다 레디스 요청, 부하가 생김
//     * Redisson: RLOCK 인터페이스 제공: 분산 락 쉽게 구현 가능, pub,sub 구조로 개선, 부하를 줄일 수 있음
//     * Redis를 사용하면 분산 락을 통한 동시성 문제와 캐시를 둘 다 잡을 수 있다.a
//     **/
//    /*
//    분산 락 사용을 위한 Redisson  클라이언트 사용
//     */
//    @Value("${spring.redis.host}")
//    private String host;
//
//    @Value("${spring.redis.port}")
//    private int port;
//
//    @Bean(name = "lettuceCacheFactory")
//    public RedisConnectionFactory redisConnectionFactory() {
//        return new LettuceConnectionFactory(host,port);
//    }
//
//    @Bean
//    public CacheManager cacheManager(@Qualifier RedisConnectionFactory redisConnectionFactory) {
//
//        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
//                .fromConnectionFactory(redisConnectionFactory);
//
//        RedisCacheConfiguration config = RedisCacheConfiguration
//                .defaultCacheConfig()
//                //캐시 키 직렬화 설정
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                // 값 직렬화 설정
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
//                //캐시 프리픽스
//                .prefixCacheNameWith("TestCache")
//                //TTL
//                .entryTtl(Duration.ofMinutes(3L));
//        builder.cacheDefaults(config);
//        return builder.build();
//    }
//
//    @Bean
//    public ObjectMapper objectMapper() {
//        //역 직렬화 시 클래스 이름 기반 하위 유형의 유효성 검사를 처리하는 클래스용 인터페이스, 기본 유형 지정을 통해 사용
//        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
//                .allowIfSubType(Object.class)
//                .build();
//
//        ObjectMapper objectMapper = new ObjectMapper()
//                //LocalDateTime 기반 역직렬화 지원 모듈 추가
//                .registerModule(new JavaTimeModule())
//                //날짜를 문자열로 표시
//                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//                //직렬화 시 기본적으로 타입 정보를 함께 저장한다.
//                .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL);
//
//        return objectMapper;
//
//    }
//
//}
