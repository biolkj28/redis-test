package com.redis.test.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Version;

@Entity
public class LectureForOptimisticLock extends BaseLecture {

    @Version
    Long version;
}
