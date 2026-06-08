package io.yar.yar2026.npc.dto;

import io.yar.yar2026.npc.domain.Npc;

import java.util.List;

public record NpcResponse(
        Long npcId,
        String rId,
        String name,
        String description,
        String locationKey,
        boolean active,
        List<NpcShopItemResponse> shopItems
) {

    public static NpcResponse from(Npc npc) {
        return from(npc, List.of());
    }

    public static NpcResponse from(Npc npc, List<NpcShopItemResponse> shopItems) {
        return new NpcResponse(
                npc.getNpcId(),
                npc.getRId(),
                npc.getName(),
                npc.getDescription(),
                npc.getLocationKey(),
                npc.isActive(),
                shopItems
        );
    }
}
