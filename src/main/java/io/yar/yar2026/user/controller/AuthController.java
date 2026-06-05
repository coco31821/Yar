package io.yar.yar2026.user.controller;

import io.yar.yar2026.common.dto.ApiResponse;
import io.yar.yar2026.user.dto.LoginRequest;
import io.yar.yar2026.user.dto.LoginResponse;
import io.yar.yar2026.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ){
        LoginResponse response = userService.login(request);

        return ApiResponse.ok(response,"로그인되었습니다.");
    }
}
