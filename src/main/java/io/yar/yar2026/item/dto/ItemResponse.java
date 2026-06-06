package io.yar.yar2026.item.dto;

import io.yar.yar2026.item.domain.Item;

import java.time.format.DateTimeFormatter;

public record ItemResponse(
        Long itemId,
        String rId,
        String itemName,
        io.yar.yar2026.item.domain.ItemType itemType,
        io.yar.yar2026.item.domain.ItemGrade itemGrade,
        String itemDescription,
        int itemPrice,
        int itemSellPrice
) {

    private static final DateTimeFormatter FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public static ItemResponse from(Item item) {
        return new ItemResponse(
          item.getItemId(),
                item.getRId(),
                item.getItemName(),
                item.getItemType(),
                item.getItemGrade(),
                item.getDescription(),
                item.getPrice(),
                item.getSellPrice()
        );
    }


}
