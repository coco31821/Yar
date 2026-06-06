package io.yar.yar2026.npc.domain;

import io.yar.yar2026.common.BaseEntity;
import io.yar.yar2026.item.domain.Item;
import io.yar.yar2026.wallet.domain.CurrencyType;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(
        name = "npc_sale_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_npc_sale_item",
                        columnNames = {"npc_id", "item_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NpcSaleItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long npcSaleItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "npc_id", nullable = false)
    private Npc npc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currencyType;


    private Integer stockQuantity;

    @Column(nullable = false)
    private int sortOrder;

    @Column(nullable = false)
    private boolean active;

    private LocalDateTime saleStartAt;

    private LocalDateTime saleEndAt;

    @Builder
    public NpcSaleItem(
            Npc npc,
            Item item,
            Integer price,
            CurrencyType currencyType,
            Integer stockQuantity,
            Integer sortOrder,
            Boolean active,
            LocalDateTime saleStartAt,
            LocalDateTime saleEndAt
    ) {
        this.npc = npc;
        this.item = item;
        this.price = price == null ? item.getPrice() : price;
        this.currencyType = currencyType == null ? CurrencyType.GOLD : currencyType;
        this.stockQuantity = stockQuantity;
        this.sortOrder = sortOrder == null ? 0 : sortOrder;
        this.active = active == null || active;
        this.saleStartAt = saleStartAt;
        this.saleEndAt = saleEndAt;
    }
}
