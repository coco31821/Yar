package io.yar.yar2026.npc.dto;

import jakarta.validation.constraints.Min;

public record NpcPurchaseRequest(
        @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
        Integer quantity
) {

    public int purchaseQuantity() {
        return quantity == null ? 1 : quantity;
    }
}
