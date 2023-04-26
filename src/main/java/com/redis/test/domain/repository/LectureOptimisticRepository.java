package com.redis.test.domain.repository;

import com.redis.test.domain.entity.LectureForOptimisticLock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureOptimisticRepository extends JpaRepository<LectureForOptimisticLock,Long> {
}
