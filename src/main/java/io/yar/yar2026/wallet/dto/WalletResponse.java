package io.yar.yar2026.wallet.dto;

import io.yar.yar2026.wallet.domain.Wallet;

public record WalletResponse(
        long gold,
        long gem
) {

    public static WalletResponse from(Wallet wallet) {
        return new WalletResponse(
                wallet.getGold(),
                wallet.getGem()
        );
    }
}
