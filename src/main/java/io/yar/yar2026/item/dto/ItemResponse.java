package io.yar.yar2026.item.dto;

import io.yar.yar2026.item.domain.Item;

public record ItemResponse(
        Long itemId,
        String rId,
        String itemName,
        String itemType,
        String itemGrade,
        String description,
        int price,
        int sellPrice
) {

    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getItemId(),
                item.getRId(),
                item.getItemName(),
                item.getItemType().name(),
                item.getItemGrade().name(),
                item.getDescription(),
                item.getPrice(),
                item.getSellPrice()
        );
    }


}
