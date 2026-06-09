package io.yar.yar2026.enhancement.dto;

import io.yar.yar2026.inventory.dto.UserItemResponse;

public record EnhancementResultResponse(
        String outcome,
        boolean miracleTimeApplied,
        int gradeBefore,
        int gradeAfter,
        int goldSpent,
        long remainingGold,
        UserItemResponse userItem
) {
}