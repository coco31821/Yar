package io.yar.yar2026.npc.dto;

import io.yar.yar2026.inventory.domain.UserItem;
import io.yar.yar2026.inventory.dto.UserItemResponse;
import io.yar.yar2026.wallet.domain.Wallet;
import io.yar.yar2026.wallet.dto.WalletResponse;

import java.util.List;

public record NpcPurchaseResponse(
        WalletResponse wallet,
        UserItemResponse acquiredItem
) {

    public static NpcPurchaseResponse of(
            Wallet wallet,
            List<UserItem> userItems
    ) {
        return new NpcPurchaseResponse(
                WalletResponse.from(wallet),
                UserItemResponse.from(userItems.get(0))
        );
    }
}
