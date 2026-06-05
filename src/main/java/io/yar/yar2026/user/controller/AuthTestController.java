package io.yar.yar2026.user.controller;

import io.yar.yar2026.common.dto.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthTestController {

    @GetMapping("/api/v1/auth/me")
    public ApiResponse<String> me(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        return ApiResponse.ok(
                "현재 로그인한 userId = " + userId,
                "인증 성공"
        );
    }
}