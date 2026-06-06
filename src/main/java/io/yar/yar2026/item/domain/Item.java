package io.yar.yar2026.item.domain;

import io.yar.yar2026.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    @Column(nullable = false, unique = true)
    private String rId;

    @Column(nullable = false)
    private String itemName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType itemType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemGrade itemGrade;

    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int sellPrice;

    @Builder
    public Item(
            String rId,
            String itemName,
            ItemType itemType,
            ItemGrade itemGrade,
            String description,
            int price,
            int sellPrice
    ) {
        this.rId = rId;
        this.itemName = itemName;
        this.itemType = itemType;
        this.itemGrade = itemGrade;
        this.description = description;
        this.price = price;
        this.sellPrice = sellPrice;
    }
}
