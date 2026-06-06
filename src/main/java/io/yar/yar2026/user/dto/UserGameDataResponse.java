package io.yar.yar2026.user.dto;

import io.yar.yar2026.inventory.dto.UserItemResponse;
import io.yar.yar2026.profile.dto.ProfileResponse;
import io.yar.yar2026.wallet.dto.WalletResponse;

import java.util.List;

public record UserGameDataResponse(
        UserResponse account,
        ProfileResponse profile,
        WalletResponse wallet,
        List<UserItemResponse> inventory,
        long friendCount
) {
}
