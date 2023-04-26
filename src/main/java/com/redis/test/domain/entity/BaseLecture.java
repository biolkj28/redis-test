package com.redis.test.domain.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@MappedSuperclass
@Getter
@Setter
public class BaseLecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column
    @ColumnDefault("10")
    private Integer maxParticipant;

    public void decrease() {
        if (maxParticipant > 0) {
            this.maxParticipant -= 1;
        }else throw new IllegalStateException("수강 인원이 가득 찼습니다.");
    }
}
