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

import java.math.BigDecimal;

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

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal successRate;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal failRate;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal destroyRate;

    @Column(nullable = false)
    private int goldCost;

    @Column(nullable = false)
    private boolean active;

    @Builder
    public EnhancementRule(
            int fromGrade,
            int toGrade,
            BigDecimal successRate,
            BigDecimal failRate,
            BigDecimal destroyRate,
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
