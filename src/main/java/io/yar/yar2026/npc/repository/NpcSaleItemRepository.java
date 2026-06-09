package io.yar.yar2026.npc.repository;

import io.yar.yar2026.npc.domain.NpcSaleItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface NpcSaleItemRepository extends JpaRepository<NpcSaleItem, Long> {

    @EntityGraph(attributePaths = {"npc", "item"})
    Optional<NpcSaleItem> findByNpc_NpcIdAndNpcSaleItemIdAndActiveTrue(
            Long npcId,
            Long npcSaleItemId
    );
    List<NpcSaleItem> findAllByNpc_NpcIdAndActiveTrueOrderBySortOrderAsc(Long npcId);

    List<NpcSaleItem> findAllByNpc_NpcIdInAndActiveTrueOrderBySortOrderAsc(Collection<Long> npcIds);
}
