package io.yar.yar2026.user.dto;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
}
