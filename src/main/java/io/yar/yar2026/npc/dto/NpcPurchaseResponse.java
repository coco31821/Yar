package io.yar.yar2026.npc.dto;

import io.yar.yar2026.inventory.domain.UserItem;
import io.yar.yar2026.inventory.dto.UserItemResponse;
import io.yar.yar2026.item.domain.Item;
import io.yar.yar2026.npc.domain.NpcSaleItem;

import java.util.List;

public record NpcPurchaseResponse(
        Long npcId,
        Long npcItemId,
        Long itemId,
        String itemName,
        int quantity,
        int goldSpent,
        long remainingGold,
        List<UserItemResponse> userItems
) {

    public static NpcPurchaseResponse of(
            NpcSaleItem npcSaleItem,
            int quantity,
            int goldSpent,
            long remainingGold,
            List<UserItem> userItems
    ) {
        Item item = npcSaleItem.getItem();

        return new NpcPurchaseResponse(
                npcSaleItem.getNpc().getNpcId(),
                npcSaleItem.getNpcSaleItemId(),
                item.getItemId(),
                item.getItemName(),
                quantity,
                goldSpent,
                remainingGold,
                userItems.stream()
                        .map(UserItemResponse::from)
                        .toList()
        );
    }
}
