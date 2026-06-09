package io.yar.yar2026.inventory.controller;

import io.yar.yar2026.common.dto.ApiResponse;
import io.yar.yar2026.inventory.dto.ItemPickupRequest;
import io.yar.yar2026.inventory.dto.UserItemResponse;
import io.yar.yar2026.inventory.service.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me/inventory")
@RequiredArgsConstructor
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    // 아이템 획득
    @PostMapping("/pickup")
    public ApiResponse<UserItemResponse> pickup(
            Authentication authentication,
            @Valid @RequestBody ItemPickupRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        UserItemResponse response = inventoryService.pickup(userId, request);

        return ApiResponse.ok(response, "아이템을 획득했습니다.");
    }

    // 인벤토리 조회
    @GetMapping
    public ApiResponse<List<UserItemResponse>> getInventory(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();

        List<UserItemResponse> response = inventoryService.getInventory(userId);

        return ApiResponse.ok(response, null);
    }

    // 아이템 버리기
    @DeleteMapping("/{userItemId}/discard")
    public ApiResponse<Void> discard(
            Authentication authentication,
            @PathVariable Long userItemId,
            @RequestParam @Min(value = 1, message = "수량은 1 이상이어야 합니다.") int quantity
    ) {
        Long userId = (Long) authentication.getPrincipal();

        inventoryService.discard(userId, userItemId, quantity);

        return ApiResponse.ok(null, "아이템을 버렸습니다.");
    }

}