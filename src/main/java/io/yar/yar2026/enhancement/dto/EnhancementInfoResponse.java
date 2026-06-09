package io.yar.yar2026.enhancement.dto;

public record EnhancementInfoResponse(
        Long userItemId,
        int currentGrade,
        int maxGrade,
        boolean maxed,
        int successRate,
        int failRate,
        int destroyRate,
        int goldCost,
        boolean miracleTime
) {
}
