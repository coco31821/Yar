package io.yar.yar2026.npc;

import io.yar.yar2026.npc.domain.Npc;
import io.yar.yar2026.npc.dto.NpcResponse;
import io.yar.yar2026.npc.exception.NpcNotFoundException;
import io.yar.yar2026.npc.repository.NpcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NpcService {

    private final NpcRepository npcRepository;

    public NpcResponse getNpc(Long npcId) {
        Npc npc = npcRepository.findByNpcIdAndActiveTrue(npcId)
                .orElseThrow(NpcNotFoundException::new);

        return NpcResponse.from(npc);
    }

    public List<NpcResponse> getNpcs() {
        return npcRepository.findByActiveTrue()
                .stream()
                .map(NpcResponse::from)
                .toList();
    }
}