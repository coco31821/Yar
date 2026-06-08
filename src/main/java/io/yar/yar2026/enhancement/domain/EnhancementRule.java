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

@Entity
@Getter
@Table(name = "enhancement_rules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnhancementRule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enhancementRuleId;

    @Column(nullable = false, unique = true)
    private int fromGrade;

    @Column(nullable = false)
    private int toGrade;

    @Column(nullable = false)
    private int successRate;

    @Column(nullable = false)
    private int failRate;

    @Column(nullable = false)
    private int destroyRate;

    @Column(nullable = false)
    private int goldCost;

    @Column(nullable = false)
    private boolean active;

    @Builder
    public EnhancementRule(
            int fromGrade,
            int toGrade,
            int successRate,
            int failRate,
            int destroyRate,
            int goldCost,
            Boolean active
    ) {
        this.fromGrade = fromGrade;
        this.toGrade = toGrade;
        this.successRate = successRate;
        this.failRate = failRate;
        this.destroyRate = destroyRate;
        this.goldCost = goldCost;
        this.active = active == null || active;
    }
}
