package io.yar.yar2026.friend.dto;

import jakarta.validation.constraints.NotNull;

public record FriendRequestCreateRequest(
        @NotNull(message = "요청 대상 유저 ID는 필수입니다.")
        Long toUserId
) {
}
