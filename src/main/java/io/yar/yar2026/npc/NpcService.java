package io.yar.yar2026.npc;

import io.yar.yar2026.npc.domain.Npc;
import io.yar.yar2026.npc.domain.NpcSaleItem;
import io.yar.yar2026.npc.dto.NpcResponse;
import io.yar.yar2026.npc.dto.NpcShopItemResponse;
import io.yar.yar2026.npc.exception.NpcNotFoundException;
import io.yar.yar2026.npc.repository.NpcRepository;
import io.yar.yar2026.npc.repository.NpcSaleItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NpcService {

    private final NpcRepository npcRepository;
    private final NpcSaleItemRepository npcSaleItemRepository;

    public NpcResponse getNpc(Long npcId) {
        Npc npc = npcRepository.findByNpcIdAndActiveTrue(npcId)
                .orElseThrow(NpcNotFoundException::new);

        List<NpcShopItemResponse> shopItems = npcSaleItemRepository
                .findAllByNpc_NpcIdAndActiveTrueOrderBySortOrderAsc(npcId)
                .stream()
                .map(NpcShopItemResponse::from)
                .toList();

        return NpcResponse.from(npc, shopItems);
    }

    public List<NpcResponse> getNpcs() {
        List<Npc> npcs = npcRepository.findByActiveTrue();
        List<Long> npcIds = npcs.stream()
                .map(Npc::getNpcId)
                .toList();

        if (npcIds.isEmpty()) {
            return List.of();
        }

        Map<Long, List<NpcShopItemResponse>> shopItemsByNpcId = npcSaleItemRepository
                .findAllByNpc_NpcIdInAndActiveTrueOrderBySortOrderAsc(npcIds)
                .stream()
                .collect(Collectors.groupingBy(
                        npcSaleItem -> npcSaleItem.getNpc().getNpcId(),
                        Collectors.mapping(NpcShopItemResponse::from, Collectors.toList())
                ));

        return npcs.stream()
                .map(npc -> NpcResponse.from(
                        npc,
                        shopItemsByNpcId.getOrDefault(npc.getNpcId(), List.of())
                ))
                .toList();
    }
}
