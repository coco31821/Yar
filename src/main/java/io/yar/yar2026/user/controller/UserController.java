package io.yar.yar2026.user.controller;

import io.yar.yar2026.common.dto.ApiResponse;
import io.yar.yar2026.profile.dto.ProfileResponse;
import io.yar.yar2026.user.dto.SignupRequest;
import io.yar.yar2026.user.dto.UserCreateResponse;
import io.yar.yar2026.user.dto.UserGameDataResponse;
import io.yar.yar2026.user.dto.UserResponse;
import io.yar.yar2026.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<UserCreateResponse> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        UserCreateResponse response = userService.signup(request);

        return ApiResponse.created(response, "가입되었습니다.");
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMe(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        UserResponse response = userService.getMe(userId);

        return ApiResponse.ok(response, null);
    }

    @GetMapping("/me/data")
    public ApiResponse<UserGameDataResponse> getMyData(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        UserGameDataResponse response = userService.getMyData(userId);

        return ApiResponse.ok(response, null);
    }

    @GetMapping("/me/profile")
    public ApiResponse<ProfileResponse> getProfile(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        ProfileResponse response = userService.getProfile(userId);

        return ApiResponse.ok(response, null);
    }

}
