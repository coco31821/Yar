package io.yar.yar2026.npc.dto;

import io.yar.yar2026.npc.domain.Npc;

public record NpcResponse(
        Long npcId,
        String rId,
        String name,
        String description,
        String locationKey,
        boolean active
) {

    public static NpcResponse from(Npc npc) {
        return new NpcResponse(
                npc.getNpcId(),
                npc.getRId(),
                npc.getName(),
                npc.getDescription(),
                npc.getLocationKey(),
                npc.isActive()
        );
    }
}