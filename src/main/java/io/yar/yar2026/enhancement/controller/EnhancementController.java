package io.yar.yar2026.enhancement.controller;

import io.yar.yar2026.common.dto.ApiResponse;
import io.yar.yar2026.enhancement.dto.EnhancementInfoResponse;
import io.yar.yar2026.enhancement.dto.EnhancementResultResponse;
import io.yar.yar2026.enhancement.service.EnhancementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me/inventory")
@RequiredArgsConstructor
public class EnhancementController {

    private final EnhancementService enhancementService;

    @GetMapping("/{userItemId}/enhance")
    public ApiResponse<EnhancementInfoResponse> getEnhancementInfo(
            Authentication authentication,
            @PathVariable Long userItemId
    ) {
        Long userId = (Long) authentication.getPrincipal();

        EnhancementInfoResponse response = enhancementService.getEnhancementInfo(userId, userItemId);

        return ApiResponse.ok(response, "강화 정보를 조회했습니다.");
    }

    @PostMapping("/{userItemId}/enhance")
    public ApiResponse<EnhancementResultResponse> enhance(
            Authentication authentication,
            @PathVariable Long userItemId
    ) {
        Long userId = (Long) authentication.getPrincipal();

        EnhancementResultResponse response = enhancementService.enhance(userId, userItemId);

        return ApiResponse.ok(response, "강화가 완료되었습니다.");
    }
}
