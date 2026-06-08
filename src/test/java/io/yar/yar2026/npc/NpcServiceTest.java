package io.yar.yar2026.npc;

import io.yar.yar2026.item.domain.Item;
import io.yar.yar2026.item.domain.ItemGrade;
import io.yar.yar2026.item.domain.ItemType;
import io.yar.yar2026.npc.domain.Npc;
import io.yar.yar2026.npc.domain.NpcSaleItem;
import io.yar.yar2026.npc.dto.NpcResponse;
import io.yar.yar2026.npc.exception.NpcNotFoundException;
import io.yar.yar2026.npc.repository.NpcRepository;
import io.yar.yar2026.npc.repository.NpcSaleItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("NpcService")
class NpcServiceTest {

    @InjectMocks
    private NpcService npcService;

    @Mock
    private NpcRepository npcRepository;

    @Mock
    private NpcSaleItemRepository npcSaleItemRepository;

    @Nested
    @DisplayName("NPC 목록 조회")
    class GetNpcs {

        @Test
        @DisplayName("전체 NPC를 응답 DTO 목록으로 반환한다")
        void getNpcs_success() {
            // given
            Npc merchant = npcOf(
                    1L,
                    "npc_001",
                    "상인",
                    "마을의 잡화 상인입니다.",
                    "VILLAGE",
                    true
            );

            Npc blacksmith = npcOf(
                    2L,
                    "npc_002",
                    "대장장이",
                    "무기를 강화해주는 대장장이입니다.",
                    "FORGE",
                    true
            );

            given(npcRepository.findByActiveTrue())
                    .willReturn(List.of(merchant, blacksmith));
            given(npcSaleItemRepository.findAllByNpc_NpcIdInAndActiveTrueOrderBySortOrderAsc(List.of(1L, 2L)))
                    .willReturn(List.of(npcSaleItemOf(
                            10L,
                            merchant,
                            itemOf(
                                    100L,
                                    "item_potion",
                                    "체력 물약",
                                    ItemType.CONSUMABLE,
                                    ItemGrade.COMMON,
                                    "체력을 회복합니다.",
                                    100,
                                    50
                            ),
                            80,
                            20,
                            1,
                            true
                    )));

            // when
            List<NpcResponse> response = npcService.getNpcs();

            // then
            assertThat(response).hasSize(2);
            assertThat(response.get(0).npcId()).isEqualTo(1L);
            assertThat(response.get(0).rId()).isEqualTo("npc_001");
            assertThat(response.get(0).name()).isEqualTo("상인");
            assertThat(response.get(0).description()).isEqualTo("마을의 잡화 상인입니다.");
            assertThat(response.get(0).locationKey()).isEqualTo("VILLAGE");
            assertThat(response.get(0).active()).isTrue();
            assertThat(response.get(0).shopItems()).hasSize(1);
            assertThat(response.get(0).shopItems().get(0).npcItemId()).isEqualTo(10L);
            assertThat(response.get(0).shopItems().get(0).itemId()).isEqualTo(100L);
            assertThat(response.get(0).shopItems().get(0).itemName()).isEqualTo("체력 물약");
            assertThat(response.get(0).shopItems().get(0).price()).isEqualTo(80);
            assertThat(response.get(0).shopItems().get(0).quantity()).isEqualTo(20);
            assertThat(response.get(1).npcId()).isEqualTo(2L);
            assertThat(response.get(1).shopItems()).isEmpty();

            then(npcRepository).should().findByActiveTrue();
            then(npcSaleItemRepository).should()
                    .findAllByNpc_NpcIdInAndActiveTrueOrderBySortOrderAsc(List.of(1L, 2L));
        }
    }

    @Nested
    @DisplayName("NPC 단건 조회")
    class GetNpc {

        @Test
        @DisplayName("존재하는 NPC이면 응답 DTO를 반환한다")
        void getNpc_success() {
            // given
            Npc npc = npcOf(
                    1L,
                    "npc_001",
                    "상인",
                    "마을의 잡화 상인입니다.",
                    "VILLAGE",
                    true
            );

            given(npcRepository.findByNpcIdAndActiveTrue(1L))
                    .willReturn(Optional.of(npc));
            given(npcSaleItemRepository.findAllByNpc_NpcIdAndActiveTrueOrderBySortOrderAsc(1L))
                    .willReturn(List.of(npcSaleItemOf(
                            10L,
                            npc,
                            itemOf(
                                    100L,
                                    "item_potion",
                                    "체력 물약",
                                    ItemType.CONSUMABLE,
                                    ItemGrade.COMMON,
                                    "체력을 회복합니다.",
                                    100,
                                    50
                            ),
                            80,
                            20,
                            1,
                            true
                    )));

            // when
            NpcResponse response = npcService.getNpc(1L);

            // then
            assertThat(response.npcId()).isEqualTo(1L);
            assertThat(response.rId()).isEqualTo("npc_001");
            assertThat(response.name()).isEqualTo("상인");
            assertThat(response.description()).isEqualTo("마을의 잡화 상인입니다.");
            assertThat(response.locationKey()).isEqualTo("VILLAGE");
            assertThat(response.active()).isTrue();
            assertThat(response.shopItems()).hasSize(1);
            assertThat(response.shopItems().get(0).npcItemId()).isEqualTo(10L);
            assertThat(response.shopItems().get(0).rId()).isEqualTo("item_potion");
            assertThat(response.shopItems().get(0).itemType()).isEqualTo(ItemType.CONSUMABLE);
            assertThat(response.shopItems().get(0).itemGrade()).isEqualTo(ItemGrade.COMMON);

            then(npcRepository).should().findByNpcIdAndActiveTrue(1L);
            then(npcSaleItemRepository).should()
                    .findAllByNpc_NpcIdAndActiveTrueOrderBySortOrderAsc(1L);
        }

        @Test
        @DisplayName("존재하지 않는 NPC이면 NpcNotFoundException이 발생한다")
        void getNpc_notFound() {
            // given
            given(npcRepository.findByNpcIdAndActiveTrue(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> npcService.getNpc(999L))
                    .isInstanceOf(NpcNotFoundException.class)
                    .hasMessage("NPC를 찾을 수 없습니다.");

            then(npcRepository).should().findByNpcIdAndActiveTrue(999L);
        }
    }

    private Npc npcOf(
            Long npcId,
            String rId,
            String name,
            String description,
            String locationKey,
            boolean active
    ) {
        Npc npc = Npc.builder()
                .rId(rId)
                .name(name)
                .description(description)
                .locationKey(locationKey)
                .active(active)
                .build();

        ReflectionTestUtils.setField(npc, "npcId", npcId);

        return npc;
    }

    private Item itemOf(
            Long itemId,
            String rId,
            String itemName,
            ItemType itemType,
            ItemGrade itemGrade,
            String description,
            int price,
            int sellPrice
    ) {
        Item item = Item.builder()
                .rId(rId)
                .itemName(itemName)
                .itemType(itemType)
                .itemGrade(itemGrade)
                .description(description)
                .price(price)
                .sellPrice(sellPrice)
                .build();

        ReflectionTestUtils.setField(item, "itemId", itemId);

        return item;
    }

    private NpcSaleItem npcSaleItemOf(
            Long npcSaleItemId,
            Npc npc,
            Item item,
            int price,
            Integer stockQuantity,
            int sortOrder,
            boolean active
    ) {
        NpcSaleItem npcSaleItem = NpcSaleItem.builder()
                .npc(npc)
                .item(item)
                .price(price)
                .stockQuantity(stockQuantity)
                .sortOrder(sortOrder)
                .active(active)
                .build();

        ReflectionTestUtils.setField(npcSaleItem, "npcSaleItemId", npcSaleItemId);

        return npcSaleItem;
    }
}
