package io.yar.yar2026.user.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        Long accessExpiresInSeconds
)
{

}
