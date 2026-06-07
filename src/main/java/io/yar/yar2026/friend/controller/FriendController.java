package io.yar.yar2026.friend.controller;

import io.yar.yar2026.common.dto.ApiResponse;
import io.yar.yar2026.friend.FriendService;
import io.yar.yar2026.friend.dto.FriendRequestCreateRequest;
import io.yar.yar2026.friend.dto.FriendRequestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/requests")
    public ApiResponse<FriendRequestResponse> createFriendRequest(
            Authentication authentication,
            @Valid @RequestBody FriendRequestCreateRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        FriendRequestResponse response = friendService.createFriendRequest(userId, request);

        return ApiResponse.ok(response, "친구 요청을 보냈습니다.");
    }
}
