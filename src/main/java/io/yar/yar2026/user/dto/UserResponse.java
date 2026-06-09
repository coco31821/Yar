package io.yar.yar2026.user.dto;

import io.yar.yar2026.user.domain.User;

import java.time.format.DateTimeFormatter;

public record UserResponse(
        Long userId,
        String email,
        String nickname,
        String role,
        String status,
        String provider,
        String profileImageUrl,
        String createdAt,
        String lastLoginAt
) {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole().name(),
                user.getStatus(),
                user.getProvider(),
                user.getProfileImageUrl(),
                user.getCreatedAt() == null ? null : user.getCreatedAt().format(FORMATTER),
                user.getLastLoginAt() == null ? null : user.getLastLoginAt().format(FORMATTER)
        );
    }
}
