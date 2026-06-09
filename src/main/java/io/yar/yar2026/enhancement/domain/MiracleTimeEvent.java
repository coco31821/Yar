package io.yar.yar2026.enhancement.domain;

import io.yar.yar2026.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "miracle_time_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MiracleTimeEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long miracleTimeEventId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false)
    private int bonusGradeStep;

    @Column(nullable = false)
    private String noticeMessage;

    @Column(nullable = false)
    private boolean active;

    @Builder
    public MiracleTimeEvent(
            String name,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Integer bonusGradeStep,
            String noticeMessage,
            Boolean active
    ) {
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
        this.bonusGradeStep = bonusGradeStep == null ? 2 : bonusGradeStep;
        this.noticeMessage = noticeMessage == null ? "강화 성공 시 등급 두배!" : noticeMessage;
        this.active = active == null || active;
    }
}
