package io.yar.yar2026.inventory.service;

import io.yar.yar2026.inventory.domain.UserItem;
import io.yar.yar2026.inventory.domain.UserItemObtainedFrom;
import io.yar.yar2026.inventory.domain.UserItemStatus;
import io.yar.yar2026.inventory.dto.ItemPickupRequest;
import io.yar.yar2026.inventory.dto.UserItemResponse;
import io.yar.yar2026.inventory.repository.UserItemRepository;
import io.yar.yar2026.item.domain.Item;
import io.yar.yar2026.item.domain.ItemGrade;
import io.yar.yar2026.item.domain.ItemType;
import io.yar.yar2026.item.exception.ItemNotFoundException;
import io.yar.yar2026.item.repository.ItemRepository;
import io.yar.yar2026.user.domain.User;
import io.yar.yar2026.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService")
class InventoryServiceTest {

    @InjectMocks
    private InventoryService inventoryService;

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserItemRepository userItemRepository;

    @Nested
    @DisplayName("아이템 획득")
    class Pickup {

        @Test
        @DisplayName("성공 시 유저 아이템을 저장하고 응답 DTO를 반환한다")
        void pickup_success() {
            // given
            User user = userOf(1L);
            Item item = itemOf(
                    1L,
                    "potion_hp_001",
                    "HP 포션",
                    ItemType.CONSUMABLE,
                    ItemGrade.COMMON,
                    "HP를 50 회복합니다.",
                    30,
                    10
            );

            ItemPickupRequest request = new ItemPickupRequest(1L, 3);

            given(userService.requireExists(1L))
                    .willReturn(user);

            given(itemRepository.findById(1L))
                    .willReturn(Optional.of(item));

            given(userItemRepository.save(any(UserItem.class)))
                    .willAnswer(invocation -> {
                        UserItem savedUserItem = invocation.getArgument(0);
                        ReflectionTestUtils.setField(savedUserItem, "userItemId", 10L);
                        return savedUserItem;
                    });

            // when
            UserItemResponse response = inventoryService.pickup(1L, request);

            // then
            assertThat(response.userItemId()).isEqualTo(10L);
            assertThat(response.itemId()).isEqualTo(1L);
            assertThat(response.rId()).isEqualTo("potion_hp_001");
            assertThat(response.itemName()).isEqualTo("HP 포션");
            assertThat(response.itemType()).isEqualTo("CONSUMABLE");
            assertThat(response.itemGrade()).isEqualTo("COMMON");
            assertThat(response.description()).isEqualTo("HP를 50 회복합니다.");
            assertThat(response.price()).isEqualTo(30);
            assertThat(response.sellPrice()).isEqualTo(10);
            assertThat(response.quantity()).isEqualTo(3);
            assertThat(response.equipped()).isFalse();
            assertThat(response.acquiredAt()).isNotBlank();

            ArgumentCaptor<UserItem> userItemCaptor = ArgumentCaptor.forClass(UserItem.class);
            then(userItemRepository).should().save(userItemCaptor.capture());

            UserItem savedUserItem = userItemCaptor.getValue();
            assertThat(savedUserItem.getUser()).isSameAs(user);
            assertThat(savedUserItem.getItem()).isSameAs(item);
            assertThat(savedUserItem.getQuantity()).isEqualTo(3);
            assertThat(savedUserItem.getStatus()).isEqualTo(UserItemStatus.OWNED);
            assertThat(savedUserItem.getEnhancementGrade()).isZero();
            assertThat(savedUserItem.getObtainedFrom()).isEqualTo(UserItemObtainedFrom.PICKUP);

            then(userService).should().requireExists(1L);
            then(itemRepository).should().findById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 아이템이면 ItemNotFoundException이 발생한다")
        void pickup_fail_when_item_not_found() {
            // given
            User user = userOf(1L);
            ItemPickupRequest request = new ItemPickupRequest(999L, 1);

            given(userService.requireExists(1L))
                    .willReturn(user);

            given(itemRepository.findById(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> inventoryService.pickup(1L, request))
                    .isInstanceOf(ItemNotFoundException.class)
                    .hasMessage("아이템을 찾을 수 없습니다.");

            then(userService).should().requireExists(1L);
            then(itemRepository).should().findById(999L);
            then(userItemRepository).should(never()).save(any(UserItem.class));
        }
    }

    private User userOf(Long userId) {
        User user = User.builder()
                .email("test@test.com")
                .password("testertestertest")
                .nickname("김승균")
                .build();

        ReflectionTestUtils.setField(user, "userId", userId);

        return user;
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
