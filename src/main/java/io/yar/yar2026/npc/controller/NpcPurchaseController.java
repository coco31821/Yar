package io.yar.yar2026.npc.controller;

import io.yar.yar2026.common.dto.ApiResponse;
import io.yar.yar2026.npc.NpcService;
import io.yar.yar2026.npc.dto.NpcPurchaseRequest;
import io.yar.yar2026.npc.dto.NpcPurchaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/me/npcs")
@RequiredArgsConstructor
public class NpcPurchaseController {

    private final NpcService npcService;

    @PostMapping("/{npcId}/items/{npcItemId}/purchase")
    public ApiResponse<NpcPurchaseResponse> purchase(
            Authentication authentication,
            @PathVariable Long npcId,
            @PathVariable Long npcItemId,
            @Valid @RequestBody(required = false) NpcPurchaseRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        NpcPurchaseResponse response = npcService.purchase(userId, npcId, npcItemId, request);

        return ApiResponse.ok(response, "구매가 완료되었습니다.");
    }
}
