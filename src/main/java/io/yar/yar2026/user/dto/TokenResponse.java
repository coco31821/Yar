package io.yar.yar2026.user.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        Long accessExpiresInSeconds
)
{

}
