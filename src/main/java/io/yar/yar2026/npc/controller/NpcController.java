package io.yar.yar2026.npc.controller;

import io.yar.yar2026.common.dto.ApiResponse;
import io.yar.yar2026.npc.NpcService;
import io.yar.yar2026.npc.dto.NpcResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/npcs")
@RequiredArgsConstructor
public class NpcController {

    private final NpcService npcService;

    @GetMapping
    public ApiResponse<List<NpcResponse>> getNpcs() {
        List<NpcResponse> response = npcService.getNpcs();

        return ApiResponse.ok(response, null);
    }

    @GetMapping("/{npcId}")
    public ApiResponse<NpcResponse> getNpc(@PathVariable Long npcId) {
        NpcResponse response = npcService.getNpc(npcId);

        return ApiResponse.ok(response, null);
    }
}