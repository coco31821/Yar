package io.yar.yar2026.inventory.dto;

import io.yar.yar2026.inventory.domain.UserItem;
import io.yar.yar2026.item.domain.Item;

import java.time.format.DateTimeFormatter;

public record UserItemResponse(
        Long userItemId,
        Long itemId,
        String rId,
        String itemName,
        String itemType,
        String itemGrade,
        String description,
        int price,
        int sellPrice,
        int quantity,
        boolean equipped,
        int enhancementGrade,
        String acquiredAt
) {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static UserItemResponse from(UserItem userItem) {
        Item item = userItem.getItem();

        return new UserItemResponse(
                userItem.getUserItemId(),
                item.getItemId(),
                item.getRId(),
                item.getItemName(),
                item.getItemType().name(),
                item.getItemGrade().name(),
                item.getDescription(),
                item.getPrice(),
                item.getSellPrice(),
                userItem.getQuantity(),
                userItem.isEquipped(),
                userItem.getEnhancementGrade(),
                userItem.getAcquiredAt() == null ? null : userItem.getAcquiredAt().format(FORMATTER)
        );
    }
}
