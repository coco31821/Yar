package io.yar.yar2026.npc;

import io.yar.yar2026.inventory.domain.UserItem;
import io.yar.yar2026.inventory.domain.UserItemObtainedFrom;
import io.yar.yar2026.inventory.domain.UserItemStatus;
import io.yar.yar2026.inventory.repository.UserItemRepository;
import io.yar.yar2026.item.domain.Item;
import io.yar.yar2026.item.domain.ItemGrade;
import io.yar.yar2026.item.domain.ItemType;
import io.yar.yar2026.npc.domain.Npc;
import io.yar.yar2026.npc.domain.NpcSaleItem;
import io.yar.yar2026.npc.dto.NpcPurchaseRequest;
import io.yar.yar2026.npc.dto.NpcPurchaseResponse;
import io.yar.yar2026.npc.dto.NpcResponse;
import io.yar.yar2026.npc.exception.NpcNotFoundException;
import io.yar.yar2026.npc.exception.ShopItemNotFoundException;
import io.yar.yar2026.npc.repository.NpcRepository;
import io.yar.yar2026.npc.repository.NpcSaleItemRepository;
import io.yar.yar2026.user.domain.User;
import io.yar.yar2026.user.service.UserService;
import io.yar.yar2026.wallet.domain.Wallet;
import io.yar.yar2026.wallet.exception.NotEnoughGoldException;
import io.yar.yar2026.wallet.repository.WalletRepository;
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
import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    private UserService userService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserItemRepository userItemRepository;

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
            assertThat(response.get(1).npcId()).isEqualTo(2L);

            then(npcRepository).should().findByActiveTrue();
        }
    }

    @Nested
    @DisplayName("NPC 판매 아이템 구매")
    class Purchase {

        @Test
        @DisplayName("소모품 구매 시 골드를 차감하고 기존 보유 수량을 증가시킨다")
        void purchase_success_when_consumable_exists() {
            // given
            User user = userOf(1L);
            Wallet wallet = walletOf(1L, user, 500L);
            Npc npc = npcOf(
                    1L,
                    "npc_001",
                    "상인",
                    "마을의 잡화 상인입니다.",
                    "VILLAGE",
                    true
            );
            Item item = itemOf(
                    10L,
                    "potion_hp_001",
                    "체력 물약",
                    ItemType.CONSUMABLE,
                    ItemGrade.COMMON,
                    "체력을 회복합니다.",
                    50,
                    15
            );
            NpcSaleItem npcSaleItem = npcSaleItemOf(
                    100L,
                    npc,
                    item,
                    50,
                    3,
                    1,
                    true
            );
            UserItem existingUserItem = userItemOf(200L, user, item, 5);

            given(userService.requireExists(1L))
                    .willReturn(user);
            given(npcSaleItemRepository.findByNpc_NpcIdAndNpcSaleItemIdAndActiveTrue(1L, 100L))
                    .willReturn(Optional.of(npcSaleItem));
            given(walletRepository.findByUser_UserId(1L))
                    .willReturn(Optional.of(wallet));
            given(userItemRepository.findByUser_UserIdAndItem_ItemIdAndEnhancementGradeAndStatus(
                    1L,
                    10L,
                    0,
                    UserItemStatus.OWNED
            )).willReturn(Optional.of(existingUserItem));

            // when
            NpcPurchaseResponse response = npcService.purchase(
                    1L,
                    1L,
                    100L,
                    new NpcPurchaseRequest(2)
            );

            // then
            assertThat(response.npcId()).isEqualTo(1L);
            assertThat(response.npcItemId()).isEqualTo(100L);
            assertThat(response.itemId()).isEqualTo(10L);
            assertThat(response.itemName()).isEqualTo("체력 물약");
            assertThat(response.quantity()).isEqualTo(2);
            assertThat(response.goldSpent()).isEqualTo(100);
            assertThat(response.remainingGold()).isEqualTo(400L);
            assertThat(response.userItems()).hasSize(1);
            assertThat(response.userItems().get(0).quantity()).isEqualTo(7);
            assertThat(wallet.getGold()).isEqualTo(400L);
            assertThat(npcSaleItem.getStockQuantity()).isEqualTo(1);
            assertThat(existingUserItem.getQuantity()).isEqualTo(7);

            then(userItemRepository).should().findByUser_UserIdAndItem_ItemIdAndEnhancementGradeAndStatus(
                    1L,
                    10L,
                    0,
                    UserItemStatus.OWNED
            );
            then(userItemRepository).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("상점 아이템이 없으면 ShopItemNotFoundException이 발생한다")
        void purchase_fail_when_shop_item_not_found() {
            // given
            User user = userOf(1L);

            given(userService.requireExists(1L))
                    .willReturn(user);
            given(npcSaleItemRepository.findByNpc_NpcIdAndNpcSaleItemIdAndActiveTrue(1L, 999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> npcService.purchase(1L, 1L, 999L, new NpcPurchaseRequest(1)))
                    .isInstanceOf(ShopItemNotFoundException.class)
                    .hasMessage("상점 아이템을 찾을 수 없습니다.");

            then(walletRepository).shouldHaveNoInteractions();
            then(userItemRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("골드가 부족하면 NotEnoughGoldException이 발생한다")
        void purchase_fail_when_gold_is_not_enough() {
            // given
            User user = userOf(1L);
            Wallet wallet = walletOf(1L, user, 50L);
            Npc npc = npcOf(
                    1L,
                    "npc_001",
                    "상인",
                    "마을의 잡화 상인입니다.",
                    "VILLAGE",
                    true
            );
            Item item = itemOf(
                    10L,
                    "potion_hp_001",
                    "체력 물약",
                    ItemType.CONSUMABLE,
                    ItemGrade.COMMON,
                    "체력을 회복합니다.",
                    50,
                    15
            );
            NpcSaleItem npcSaleItem = npcSaleItemOf(
                    100L,
                    npc,
                    item,
                    50,
                    3,
                    1,
                    true
            );

            given(userService.requireExists(1L))
                    .willReturn(user);
            given(npcSaleItemRepository.findByNpc_NpcIdAndNpcSaleItemIdAndActiveTrue(1L, 100L))
                    .willReturn(Optional.of(npcSaleItem));
            given(walletRepository.findByUser_UserId(1L))
                    .willReturn(Optional.of(wallet));

            // when & then
            assertThatThrownBy(() -> npcService.purchase(1L, 1L, 100L, new NpcPurchaseRequest(2)))
                    .isInstanceOf(NotEnoughGoldException.class)
                    .hasMessage("골드가 부족합니다.");

            assertThat(wallet.getGold()).isEqualTo(50L);
            assertThat(npcSaleItem.getStockQuantity()).isEqualTo(3);
            then(userItemRepository).shouldHaveNoInteractions();
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

            // when
            NpcResponse response = npcService.getNpc(1L);

            // then
            assertThat(response.npcId()).isEqualTo(1L);
            assertThat(response.rId()).isEqualTo("npc_001");
            assertThat(response.name()).isEqualTo("상인");
            assertThat(response.description()).isEqualTo("마을의 잡화 상인입니다.");
            assertThat(response.locationKey()).isEqualTo("VILLAGE");
            assertThat(response.active()).isTrue();

            then(npcRepository).should().findByNpcIdAndActiveTrue(1L);
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

    private User userOf(Long userId) {
        User user = User.builder()
                .email("newgamer@test.com")
                .password("password")
                .nickname("새싹게이머")
                .build();

        ReflectionTestUtils.setField(user, "userId", userId);

        return user;
    }

    private Wallet walletOf(Long walletId, User user, long gold) {
        Wallet wallet = Wallet.builder()
                .user(user)
                .gold(gold)
                .gem(0)
                .build();

        ReflectionTestUtils.setField(wallet, "walletId", walletId);

        return wallet;
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

    private UserItem userItemOf(Long userItemId, User user, Item item, int quantity) {
        UserItem userItem = UserItem.builder()
                .user(user)
                .item(item)
                .quantity(quantity)
                .status(UserItemStatus.OWNED)
                .enhancementGrade(0)
                .obtainedFrom(UserItemObtainedFrom.NPC_SHOP)
                .build();

        ReflectionTestUtils.setField(userItem, "userItemId", userItemId);

        return userItem;
    }
}
