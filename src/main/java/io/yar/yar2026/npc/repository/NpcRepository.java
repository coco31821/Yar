package io.yar.yar2026.npc.repository;

import io.yar.yar2026.npc.domain.Npc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NpcRepository extends JpaRepository<Npc, Long> {

    List<Npc> findByActiveTrue();

    Optional<Npc> findByNpcIdAndActiveTrue(Long npcId);
}