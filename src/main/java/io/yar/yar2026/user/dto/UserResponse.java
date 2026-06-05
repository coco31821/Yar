package io.yar.yar2026.user.dto;

import java.time.LocalDateTime;

public record UserResponse(
        Long userId,
        String email,
        String nickname,
        String role,
        String status,
        String provider,
        String profileImageUrl,
        String createdAt,
        String lastLoginAt) {
}
