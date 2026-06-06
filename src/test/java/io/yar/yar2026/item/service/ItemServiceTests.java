package io.yar.yar2026.item.service;

import io.yar.yar2026.item.ItemService;
import io.yar.yar2026.item.domain.Item;
import io.yar.yar2026.item.domain.ItemGrade;
import io.yar.yar2026.item.domain.ItemType;
import io.yar.yar2026.item.dto.ItemResponse;
import io.yar.yar2026.item.exception.ItemNotFoundException;
import io.yar.yar2026.item.repository.ItemRepository;
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
@DisplayName("ItemService")
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Nested
    @DisplayName("전체 아이템 목록 조회")
    class GetItems {

        @Test
        @DisplayName("전체 아이템을 응답 DTO 목록으로 반환한다")
        void getItems_success() {
            // given
            Item sword = itemOf(
                    1L,
                    "sword_001",
                    "연습용 검",
                    ItemType.WEAPON,
                    ItemGrade.COMMON,
                    "초보자용 검입니다.",
                    100,
                    50
            );

            Item potion = itemOf(
                    2L,
                    "potion_hp_001",
                    "HP 포션",
                    ItemType.CONSUMABLE,
                    ItemGrade.COMMON,
                    "HP를 50 회복합니다.",
                    30,
                    10
            );

            given(itemRepository.findAll())
                    .willReturn(List.of(sword, potion));

            // when
            List<ItemResponse> response = itemService.getItems();

            // then
            assertThat(response).hasSize(2);
            assertThat(response.get(0).itemId()).isEqualTo(1L);
            assertThat(response.get(0).rId()).isEqualTo("sword_001");
            assertThat(response.get(0).itemName()).isEqualTo("연습용 검");
            assertThat(response.get(0).itemType()).isEqualTo("WEAPON");
            assertThat(response.get(0).itemGrade()).isEqualTo("COMMON");
            assertThat(response.get(0).description()).isEqualTo("초보자용 검입니다.");
            assertThat(response.get(0).price()).isEqualTo(100);
            assertThat(response.get(0).sellPrice()).isEqualTo(50);
            assertThat(response.get(1).itemId()).isEqualTo(2L);

            then(itemRepository).should().findAll();
        }
    }

    @Nested
    @DisplayName("아이템 단건 조회")
    class GetItem {

        @Test
        @DisplayName("존재하는 아이템이면 응답 DTO를 반환한다")
        void getItem_success() {
            // given
            Item item = itemOf(
                    1L,
                    "sword_001",
                    "연습용 검",
                    ItemType.WEAPON,
                    ItemGrade.COMMON,
                    "초보자용 검입니다.",
                    100,
                    50
            );

            given(itemRepository.findById(1L))
                    .willReturn(Optional.of(item));

            // when
            ItemResponse response = itemService.getItem(1L);

            // then
            assertThat(response.itemId()).isEqualTo(1L);
            assertThat(response.rId()).isEqualTo("sword_001");
            assertThat(response.itemName()).isEqualTo("연습용 검");
            assertThat(response.itemType()).isEqualTo("WEAPON");
            assertThat(response.itemGrade()).isEqualTo("COMMON");
            assertThat(response.description()).isEqualTo("초보자용 검입니다.");
            assertThat(response.price()).isEqualTo(100);
            assertThat(response.sellPrice()).isEqualTo(50);

            then(itemRepository).should().findById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 아이템이면 ItemNotFoundException이 발생한다")
        void getItem_fail_when_item_not_found() {
            // given
            given(itemRepository.findById(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> itemService.getItem(999L))
                    .isInstanceOf(ItemNotFoundException.class)
                    .hasMessage("아이템을 찾을 수 없습니다.");

            then(itemRepository).should().findById(999L);
        }
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
}
