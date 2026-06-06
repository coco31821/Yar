package io.yar.yar2026.inventory.domain;

import io.yar.yar2026.common.BaseEntity;
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

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "user_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserItemStatus status;

    @Column(nullable = false)
    private int enhancementGrade;

    @Column(nullable = false)
    private int enhancementCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserItemObtainedFrom obtainedFrom;

    @Column(nullable = false)
    private LocalDateTime acquiredAt;

    private LocalDateTime destroyedAt;

    @Builder
    public UserItem(
            User user,
            Item item,
            int quantity,
            UserItemStatus status,
            int enhancementGrade,
            UserItemObtainedFrom obtainedFrom
    ) {
        this.user = user;
        this.item = item;
        this.quantity = quantity;
        this.status = status == null ? UserItemStatus.OWNED : status;
        this.enhancementGrade = enhancementGrade;
        this.enhancementCount = 0;
        this.obtainedFrom = obtainedFrom == null ? UserItemObtainedFrom.PICKUP : obtainedFrom;
        this.acquiredAt = LocalDateTime.now();
    }

    public boolean isEquipped() {
        return status == UserItemStatus.EQUIPPED;
    }

    public boolean isVisibleInInventory() {
        return status == UserItemStatus.OWNED || status == UserItemStatus.EQUIPPED;
    }
}
