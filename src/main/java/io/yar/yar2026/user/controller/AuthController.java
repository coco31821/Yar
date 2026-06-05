package io.yar.yar2026.user.controller;

import io.yar.yar2026.common.dto.ApiResponse;
import io.yar.yar2026.user.dto.LoginRequest;
import io.yar.yar2026.user.dto.TokenResponse;
import io.yar.yar2026.user.dto.RefreshRequest;
import io.yar.yar2026.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(
            @Valid @RequestBody LoginRequest request
    ){
        TokenResponse response = userService.login(request);

        return ApiResponse.ok(response,"로그인되었습니다.");
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refreshToken(
            @Valid @RequestBody RefreshRequest request
    ){
        TokenResponse response = userService.refresh(request);

        return ApiResponse.ok(response,null);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(Authentication authentication){
        Long userId = (Long) authentication.getPrincipal();

        userService.logout(userId);

        return ApiResponse.ok(null,"로그아웃되었습니다.");
    }



}
