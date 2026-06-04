package io.yar.yar2026.user.dto;

import io.yar.yar2026.user.constants.Role;
import io.yar.yar2026.user.domain.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record UserCreateResponse(
        Long userId,
        String email,
        String nickname,
        Role role,
        String status,
        String provider,
        String profileImageUrl,
        String createdAt,
        String lastLoginAt
) {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static UserCreateResponse from(User user) {

        return new UserCreateResponse(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole(),
                user.getStatus(),
                user.getProvider(),
                user.getProfileImageUrl(),
                user.getCreatedAt() == null ? null : user.getCreatedAt().format(FORMATTER),
                user.getLastLoginAt() == null ? null : user.getLastLoginAt().format(FORMATTER)
        );
    }
}
