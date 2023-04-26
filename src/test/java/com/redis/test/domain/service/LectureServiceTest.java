package com.redis.test.domain.service;

import com.redis.test.domain.entity.Lecture;
import com.redis.test.domain.repository.LectureOptimisticRepository;
import com.redis.test.domain.repository.LectureRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@DisplayName("Redisson Lock and Optimistic Lock Test")
@SpringBootTest
class LectureServiceTest {

    @Autowired
    private LectureService lectureService;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private LectureOptimisticRepository lectureOptimisticRepository;

    private Lecture lecture;
    private LectureForOptimisticLock lecture1;

    private Lecture lecture2;

    @BeforeEach
    void init() {

        //  분산 락 테스트용 데이터
        lecture = new Lecture();
        lecture.setName("test");
        lecture.setMaxParticipant(100);
        lecture=lectureRepository.save(lecture);

        // 낙관적 락 테스트용 데이터
        lecture1 = new LectureForOptimisticLock();
        lecture1.setName("test");
        lecture1.setMaxParticipant(100);
        lecture1=lectureOptimisticRepository.save(lecture1);

        // 비관적 락 테스트용 데이터
        lecture2 = new Lecture();
        lecture2.setName("test1");
        lecture2.setMaxParticipant(100);
        lecture2=lectureRepository.save(lecture);


    }

    @Test
    @DisplayName("분산 락 테스트")
    void lectureDecreaseUsingDisLock() throws InterruptedException {

        Long lectureId = lecture.getId();
        int threadNum = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadNum);

        for (int i = 0; i < threadNum; i++) {
            executorService.submit(() -> {
                try {

                    lectureService.lectureDecreaseUsingDisLock("Enter-" + lectureId, lectureId);

                } finally {
                    latch.countDown();
                }
            });
        }
        //쓰레드 모두 완료 될때 까지 대기
        latch.await();

        Integer count = lectureRepository.findById(lectureId).orElseThrow(IllegalAccessError::new).getMaxParticipant();
        Assertions.assertThat(count).isZero();

    }

    @Test
    @DisplayName("낙관 락 테스트")
    void lectureDecreaseUsingOptimisticLock() throws InterruptedException {

        Long lectureId = lecture1.getId();
        int threadNum = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadNum);

        for (int i = 0; i < threadNum; i++) {
            executorService.submit(() -> {
                try {

                    lectureService.lectureDecreaseUsingOptimisticLock(lectureId);

                } finally {
                    latch.countDown();
                }
            });
        }
        //쓰레드 모두 완료 될때 까지 대기
        latch.await();

        Integer count = lectureOptimisticRepository.findById(lectureId).orElseThrow(IllegalAccessError::new).getMaxParticipant();
        Assertions.assertThat(count).isZero();
    }

    @Test
    @DisplayName("비관적 락 테스트")
    void lectureDecreaseUsingPessimisticLock() throws InterruptedException {
        Long lectureId = lecture2.getId();
        int threadNum = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadNum);

        for (int i = 0; i < threadNum; i++) {
            executorService.submit(() -> {
                try {

                    lectureService.lectureDecreaseUsingPessimisticLock(lectureId);

                } finally {
                    latch.countDown();
                }
            });
        }
        //쓰레드 모두 완료 될때 까지 대기
        latch.await();

        Integer count = lectureRepository.findById(lectureId).orElseThrow(IllegalAccessError::new).getMaxParticipant();
        Assertions.assertThat(count).isZero();
    }

}