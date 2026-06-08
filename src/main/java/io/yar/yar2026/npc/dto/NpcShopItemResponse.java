package io.yar.yar2026.npc.dto;

import io.yar.yar2026.item.domain.Item;
import io.yar.yar2026.item.domain.ItemGrade;
import io.yar.yar2026.item.domain.ItemType;
import io.yar.yar2026.npc.domain.NpcSaleItem;

public record NpcShopItemResponse(
        Long npcItemId,
        Long itemId,
        String rId,
        String itemName,
        ItemType itemType,
        ItemGrade itemGrade,
        String description,
        int price,
        int sellPrice,
        Integer quantity,
        int sortOrder
) {

    public static NpcShopItemResponse from(NpcSaleItem npcSaleItem) {
        Item item = npcSaleItem.getItem();

        return new NpcShopItemResponse(
                npcSaleItem.getNpcSaleItemId(),
                item.getItemId(),
                item.getRId(),
                item.getItemName(),
                item.getItemType(),
                item.getItemGrade(),
                item.getDescription(),
                npcSaleItem.getPrice(),
                item.getSellPrice(),
                npcSaleItem.getStockQuantity(),
                npcSaleItem.getSortOrder()
        );
    }
}
