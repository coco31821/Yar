package io.yar.yar2026.profile.dto;

import io.yar.yar2026.profile.domain.Profile;

public record ProfileResponse(
        int level,
        long exp,
        long totalPlaySeconds
) {

    public static ProfileResponse from(Profile profile) {
        return new ProfileResponse(
                profile.getLevel(),
                profile.getExp(),
                profile.getTotalPlaySeconds()
        );
    }
}
