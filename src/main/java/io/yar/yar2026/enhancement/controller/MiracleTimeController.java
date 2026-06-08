package io.yar.yar2026.enhancement.controller;

import io.yar.yar2026.common.dto.ApiResponse;
import io.yar.yar2026.enhancement.dto.MiracleTimeResponse;
import io.yar.yar2026.enhancement.service.EnhancementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MiracleTimeController {

    private final EnhancementService enhancementService;

    @GetMapping("/api/v1/enhancement/miracle-time")
    public ApiResponse<MiracleTimeResponse> getMiracleTime() {
        MiracleTimeResponse response = enhancementService.getMiracleTime();

        return ApiResponse.ok(response, "미라클 타임 정보를 조회했습니다.");
    }
}