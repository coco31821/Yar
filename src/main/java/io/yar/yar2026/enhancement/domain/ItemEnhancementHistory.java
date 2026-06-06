package io.yar.yar2026.enhancement.domain;

import io.yar.yar2026.common.BaseEntity;
import io.yar.yar2026.inventory.domain.UserItem;
import io.yar.yar2026.item.domain.Item;
import io.yar.yar2026.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "item_enhancement_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemEnhancementHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemEnhancementHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_item_id", nullable = false)
    private UserItem userItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private int beforeGrade;

    @Column(nullable = false)
    private int afterGrade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnhancementOutcome outcome;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal successRate;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal failRate;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal destroyRate;

    @Column(nullable = false)
    private int goldSpent;

    @Column(nullable = false)
    private boolean miracleApplied;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "miracle_time_event_id")
    private MiracleTimeEvent miracleTimeEvent;

    @Builder
    public ItemEnhancementHistory(
            User user,
            UserItem userItem,
            Item item,
            int beforeGrade,
            int afterGrade,
            EnhancementOutcome outcome,
            BigDecimal successRate,
            BigDecimal failRate,
            BigDecimal destroyRate,
            int goldSpent,
            boolean miracleApplied,
            MiracleTimeEvent miracleTimeEvent
    ) {
        this.user = user;
        this.userItem = userItem;
        this.item = item;
        this.beforeGrade = beforeGrade;
        this.afterGrade = afterGrade;
        this.outcome = outcome;
        this.successRate = successRate;
        this.failRate = failRate;
        this.destroyRate = destroyRate;
        this.goldSpent = goldSpent;
        this.miracleApplied = miracleApplied;
        this.miracleTimeEvent = miracleTimeEvent;
    }
}
