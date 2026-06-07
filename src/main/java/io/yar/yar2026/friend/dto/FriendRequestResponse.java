package io.yar.yar2026.friend.dto;

import io.yar.yar2026.friend.domain.FriendRequest;
import io.yar.yar2026.friend.domain.FriendRequestStatus;

import java.time.LocalDateTime;

public record FriendRequestResponse(
        Long friendRequestId,
        Long fromUserId,
        Long toUserId,
        FriendRequestStatus status,
        LocalDateTime createdAt,
        String nickname
) {
    public static FriendRequestResponse from(FriendRequest friendRequest) {
        return fromSent(friendRequest);
    }

    public static FriendRequestResponse fromReceived(FriendRequest friendRequest) {
        return new FriendRequestResponse(
                friendRequest.getFriendRequestId(),
                friendRequest.getFromUser().getUserId(),
                friendRequest.getToUser().getUserId(),
                friendRequest.getStatus(),
                friendRequest.getCreatedAt(),
                friendRequest.getFromUser().getNickname()
        );
    }

    public static FriendRequestResponse fromSent(FriendRequest friendRequest) {
        return new FriendRequestResponse(
                friendRequest.getFriendRequestId(),
                friendRequest.getFromUser().getUserId(),
                friendRequest.getToUser().getUserId(),
                friendRequest.getStatus(),
                friendRequest.getCreatedAt(),
                friendRequest.getToUser().getNickname()
        );
    }
}
