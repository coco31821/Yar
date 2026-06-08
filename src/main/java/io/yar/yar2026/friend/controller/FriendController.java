package io.yar.yar2026.friend.controller;

import io.yar.yar2026.common.dto.ApiResponse;
import io.yar.yar2026.friend.FriendService;
import io.yar.yar2026.friend.dto.FriendRequestCreateRequest;
import io.yar.yar2026.friend.dto.FriendRequestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    public ApiResponse<List<FriendRequestResponse>> getFriends(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        List<FriendRequestResponse> response = friendService.getFriends(userId);

        return ApiResponse.ok(response, null);
    }

    @PostMapping("/requests")
    public ApiResponse<FriendRequestResponse> createFriendRequest(
            Authentication authentication,
            @Valid @RequestBody FriendRequestCreateRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        FriendRequestResponse response = friendService.createFriendRequest(userId, request);

        return ApiResponse.ok(response, "친구 요청을 보냈습니다.");
    }

    @GetMapping("/requests")
    public ApiResponse<List<FriendRequestResponse>> getReceivedFriendRequests(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        List<FriendRequestResponse> response = friendService.getReceivedFriendRequests(userId);

        return ApiResponse.ok(response, null);
    }

    @GetMapping("/requests/sent")
    public ApiResponse<List<FriendRequestResponse>> getSentFriendRequests(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        List<FriendRequestResponse> response = friendService.getSentFriendRequests(userId);

        return ApiResponse.ok(response, null);
    }

    @PostMapping("/requests/{requestId}/accept")
    public ApiResponse<FriendRequestResponse> acceptFriendRequest(
            Authentication authentication,
            @PathVariable Long requestId
    ) {
        Long userId = (Long) authentication.getPrincipal();

        FriendRequestResponse response = friendService.acceptFriendRequest(userId, requestId);

        return ApiResponse.ok(response, "친구 요청을 수락했습니다.");
    }

    @PostMapping("/requests/{requestId}/decline")
    public ApiResponse<Void> declineFriendRequest(
            Authentication authentication,
            @PathVariable Long requestId
    ) {
        Long userId = (Long) authentication.getPrincipal();

        friendService.declineFriendRequest(userId, requestId);

        return ApiResponse.ok(null, "친구 요청을 거절했습니다.");
    }

    @DeleteMapping("/requests/{requestId}")
    public ApiResponse<Void> cancelFriendRequest(
            Authentication authentication,
            @PathVariable Long requestId
    ) {
        Long userId = (Long) authentication.getPrincipal();

        friendService.cancelFriendRequest(userId, requestId);

        return ApiResponse.ok(null, "친구 요청을 취소했습니다.");
    }
}
