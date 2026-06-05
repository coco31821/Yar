package io.yar.yar2026.user.controller;

import io.yar.yar2026.common.dto.ApiResponse;
import io.yar.yar2026.user.dto.SignupRequest;
import io.yar.yar2026.user.dto.UserCreateResponse;
import io.yar.yar2026.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
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


}
