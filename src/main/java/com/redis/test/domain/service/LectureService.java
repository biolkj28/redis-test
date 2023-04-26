package com.redis.test.domain.service;

import com.redis.test.common.aop.DistributedLock;
import com.redis.test.domain.entity.Lecture;
import com.redis.test.domain.entity.LectureForOptimisticLock;
import com.redis.test.domain.repository.LectureOptimisticRepository;
import com.redis.test.domain.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureService {

    private final LectureRepository lectureRepository;
    private final LectureOptimisticRepository lectureOptimisticRepository;


    //분산 락
    @DistributedLock(key="#key")
    public void lectureDecreaseUsingDisLock(String key, Long lectureId){
        Lecture lecture = lectureRepository
                .findById(lectureId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 강의입니다."));

        lecture.decrease();
    }

    // 낙관적 락 @Version 적용
    @Transactional
    public void lectureDecreaseUsingOptimisticLock(Long lectureId){
        LectureForOptimisticLock lecture = lectureOptimisticRepository
                .findById(lectureId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 강의ㅎ힙니다."));
        log.info("낙관적 락 동시성 제어 테스트 카운트:{}", lecture.getMaxParticipant());
    try {
        lecture.decrease();
    }catch (ObjectOptimisticLockingFailureException e){
        lectureDecreaseUsingOptimisticLock(lectureId);
    }



    }

    //비관적 락
    @Transactional
    public void lectureDecreaseUsingPessimisticLock(Long lectureId){
        Lecture lecture = lectureRepository
                .findByIdToPessimistic(lectureId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 강의힙니다."));

        lecture.decrease();

    }


}
