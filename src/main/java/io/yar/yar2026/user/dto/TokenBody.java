package io.yar.yar2026.user.dto;

import io.yar.yar2026.user.constants.Role;

public record TokenBody(
        Long userId,
        Role role
) {
}
