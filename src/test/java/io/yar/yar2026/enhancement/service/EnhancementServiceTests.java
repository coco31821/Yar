package io.yar.yar2026.enhancement.service;

import io.yar.yar2026.enhancement.domain.EnhancementRule;
import io.yar.yar2026.enhancement.domain.MiracleTimeEvent;
import io.yar.yar2026.enhancement.dto.EnhancementInfoResponse;
import io.yar.yar2026.enhancement.exception.EnhancementMaxGradeException;
import io.yar.yar2026.enhancement.exception.EnhancementRuleNotFoundException;
import io.yar.yar2026.enhancement.exception.ItemNotEnhanceableException;
import io.yar.yar2026.enhancement.repository.EnhancementRuleRepository;
import io.yar.yar2026.enhancement.repository.MiracleTimeEventRepository;
import io.yar.yar2026.inventory.domain.UserItem;
import io.yar.yar2026.inventory.domain.UserItemObtainedFrom;
import io.yar.yar2026.inventory.domain.UserItemStatus;
import io.yar.yar2026.inventory.exception.UserItemNotFoundException;
import io.yar.yar2026.inventory.repository.UserItemRepository;
import io.yar.yar2026.item.domain.Item;
import io.yar.yar2026.item.domain.ItemGrade;
import io.yar.yar2026.item.domain.ItemType;
import io.yar.yar2026.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("EnhancementService")
class EnhancementServiceTest {

    @InjectMocks
    private EnhancementService enhancementService;

    @Mock
    private UserItemRepository userItemRepository;

    @Mock
    private EnhancementRuleRepository enhancementRuleRepository;

    @Mock
    private MiracleTimeEventRepository miracleTimeEventRepository;

    @Nested
    @DisplayName("강화창 정보 조회")
    class GetEnhancementInfo {

        @Test
        @DisplayName("성공 시 강화 확률, 비용, 미라클 타임 여부를 반환한다")
        void getEnhancementInfo_success() {
            // given
            User user = userOf(1L);
            Item item = itemOf(
                    1L,
                    "wooden_bow",
                    "나무 활",
                    ItemType.WEAPON,
                    ItemGrade.COMMON
            );
            UserItem userItem = userItemOf(10L, user, item, 3);
            EnhancementRule rule = EnhancementRule.builder()
                    .fromGrade(3)
                    .toGrade(4)
                    .successRate(55)
                    .failRate(40)
                    .destroyRate(5)
                    .goldCost(4)
                    .active(true)
                    .build();
            MiracleTimeEvent miracleTimeEvent = MiracleTimeEvent.builder()
                    .name("테스트 미라클 타임")
                    .startAt(LocalDateTime.now().minusHours(1))
                    .endAt(LocalDateTime.now().plusHours(1))
                    .bonusGradeStep(2)
                    .noticeMessage("미라클 타임 진행 중!")
                    .active(true)
                    .build();
            List<UserItemStatus> visibleStatuses = List.of(
                    UserItemStatus.OWNED,
                    UserItemStatus.EQUIPPED
            );

            given(userItemRepository.findByUserItemIdAndUser_UserIdAndStatusIn(
                    10L,
                    1L,
                    visibleStatuses
            )).willReturn(Optional.of(userItem));

            given(enhancementRuleRepository.findByFromGradeAndActiveTrue(3))
                    .willReturn(Optional.of(rule));

            given(miracleTimeEventRepository.findActiveEvent(any(LocalDateTime.class)))
                    .willReturn(Optional.of(miracleTimeEvent));

            // when
            EnhancementInfoResponse response = enhancementService.getEnhancementInfo(1L, 10L);

            // then
            assertThat(response.userItemId()).isEqualTo(10L);
            assertThat(response.currentGrade()).isEqualTo(3);
            assertThat(response.maxGrade()).isEqualTo(5);
            assertThat(response.maxed()).isFalse();
            assertThat(response.successRate()).isEqualTo(55);
            assertThat(response.failRate()).isEqualTo(40);
            assertThat(response.destroyRate()).isEqualTo(5);
            assertThat(response.goldCost()).isEqualTo(4);
            assertThat(response.miracleTime()).isTrue();

            then(userItemRepository).should()
                    .findByUserItemIdAndUser_UserIdAndStatusIn(10L, 1L, visibleStatuses);
            then(enhancementRuleRepository).should().findByFromGradeAndActiveTrue(3);
        }

        @Test
        @DisplayName("보유 아이템이 없으면 UserItemNotFoundException이 발생한다")
        void getEnhancementInfo_fail_when_user_item_not_found() {
            // given
            List<UserItemStatus> visibleStatuses = List.of(
                    UserItemStatus.OWNED,
                    UserItemStatus.EQUIPPED
            );

            given(userItemRepository.findByUserItemIdAndUser_UserIdAndStatusIn(
                    999L,
                    1L,
                    visibleStatuses
            )).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> enhancementService.getEnhancementInfo(1L, 999L))
                    .isInstanceOf(UserItemNotFoundException.class)
                    .hasMessage("아이템을 찾을 수 없습니다.");

            then(enhancementRuleRepository).shouldHaveNoInteractions();
            then(miracleTimeEventRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("강화할 수 없는 아이템이면 ItemNotEnhanceableException이 발생한다")
        void getEnhancementInfo_fail_when_item_is_not_enhanceable() {
            // given
            User user = userOf(1L);
            Item item = itemOf(
                    1L,
                    "potion_hp_001",
                    "HP 포션",
                    ItemType.CONSUMABLE,
                    ItemGrade.COMMON
            );
            UserItem userItem = userItemOf(10L, user, item, 0);
            List<UserItemStatus> visibleStatuses = List.of(
                    UserItemStatus.OWNED,
                    UserItemStatus.EQUIPPED
            );

            given(userItemRepository.findByUserItemIdAndUser_UserIdAndStatusIn(
                    10L,
                    1L,
                    visibleStatuses
            )).willReturn(Optional.of(userItem));

            // when & then
            assertThatThrownBy(() -> enhancementService.getEnhancementInfo(1L, 10L))
                    .isInstanceOf(ItemNotEnhanceableException.class)
                    .hasMessage("강화할 수 없는 아이템입니다.");

            then(enhancementRuleRepository).shouldHaveNoInteractions();
            then(miracleTimeEventRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("COMMON 아이템이 +5면 EnhancementMaxGradeException이 발생한다")
        void getEnhancementInfo_fail_when_common_item_is_already_max_grade() {
            // given
            User user = userOf(1L);
            Item item = itemOf(
                    1L,
                    "wooden_bow",
                    "나무 활",
                    ItemType.WEAPON,
                    ItemGrade.COMMON
            );
            UserItem userItem = userItemOf(10L, user, item, 5);
            List<UserItemStatus> visibleStatuses = List.of(
                    UserItemStatus.OWNED,
                    UserItemStatus.EQUIPPED
            );

            given(userItemRepository.findByUserItemIdAndUser_UserIdAndStatusIn(
                    10L,
                    1L,
                    visibleStatuses
            )).willReturn(Optional.of(userItem));

            // when & then
            assertThatThrownBy(() -> enhancementService.getEnhancementInfo(1L, 10L))
                    .isInstanceOf(EnhancementMaxGradeException.class)
                    .hasMessage("이미 최대 강화 등급입니다.");

            then(enhancementRuleRepository).shouldHaveNoInteractions();
            then(miracleTimeEventRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("LEGENDARY 아이템은 최대 강화 등급을 20으로 반환한다")
        void getEnhancementInfo_success_when_legendary_item() {
            // given
            User user = userOf(1L);
            Item item = itemOf(
                    1L,
                    "legendary_sword",
                    "전설의 검",
                    ItemType.WEAPON,
                    ItemGrade.LEGENDARY
            );
            UserItem userItem = userItemOf(10L, user, item, 16);
            EnhancementRule rule = EnhancementRule.builder()
                    .fromGrade(16)
                    .toGrade(17)
                    .successRate(10)
                    .failRate(90)
                    .destroyRate(30)
                    .goldCost(3300)
                    .active(true)
                    .build();
            List<UserItemStatus> visibleStatuses = List.of(
                    UserItemStatus.OWNED,
                    UserItemStatus.EQUIPPED
            );

            given(userItemRepository.findByUserItemIdAndUser_UserIdAndStatusIn(
                    10L,
                    1L,
                    visibleStatuses
            )).willReturn(Optional.of(userItem));

            given(enhancementRuleRepository.findByFromGradeAndActiveTrue(16))
                    .willReturn(Optional.of(rule));

            given(miracleTimeEventRepository.findActiveEvent(any(LocalDateTime.class)))
                    .willReturn(Optional.empty());

            // when
            EnhancementInfoResponse response = enhancementService.getEnhancementInfo(1L, 10L);

            // then
            assertThat(response.currentGrade()).isEqualTo(16);
            assertThat(response.maxGrade()).isEqualTo(20);
            assertThat(response.successRate()).isEqualTo(10);
            assertThat(response.failRate()).isEqualTo(90);
            assertThat(response.destroyRate()).isEqualTo(30);
            assertThat(response.goldCost()).isEqualTo(3300);
            assertThat(response.miracleTime()).isFalse();
        }

        @Test
        @DisplayName("현재 등급의 강화 규칙이 없으면 EnhancementRuleNotFoundException이 발생한다")
        void getEnhancementInfo_fail_when_rule_not_found() {
            // given
            User user = userOf(1L);
            Item item = itemOf(
                    1L,
                    "wooden_bow",
                    "나무 활",
                    ItemType.WEAPON,
                    ItemGrade.COMMON
            );
            UserItem userItem = userItemOf(10L, user, item, 3);
            List<UserItemStatus> visibleStatuses = List.of(
                    UserItemStatus.OWNED,
                    UserItemStatus.EQUIPPED
            );

            given(userItemRepository.findByUserItemIdAndUser_UserIdAndStatusIn(
                    10L,
                    1L,
                    visibleStatuses
            )).willReturn(Optional.of(userItem));

            given(enhancementRuleRepository.findByFromGradeAndActiveTrue(3))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> enhancementService.getEnhancementInfo(1L, 10L))
                    .isInstanceOf(EnhancementRuleNotFoundException.class)
                    .hasMessage("강화 규칙을 찾을 수 없습니다.");

            then(miracleTimeEventRepository).shouldHaveNoInteractions();
        }
    }

    private User userOf(Long userId) {
        User user = User.builder()
                .email("test@test.com")
                .password("password123")
                .nickname("테스터")
                .build();

        ReflectionTestUtils.setField(user, "userId", userId);

        return user;
    }

    private Item itemOf(
            Long itemId,
            String rId,
            String itemName,
            ItemType itemType,
            ItemGrade itemGrade
    ) {
        Item item = Item.builder()
                .rId(rId)
                .itemName(itemName)
                .itemType(itemType)
                .itemGrade(itemGrade)
                .description("설명")
                .price(100)
                .sellPrice(50)
                .build();

        ReflectionTestUtils.setField(item, "itemId", itemId);

        return item;
    }

    private UserItem userItemOf(
            Long userItemId,
            User user,
            Item item,
            int enhancementGrade
    ) {
        UserItem userItem = UserItem.builder()
                .user(user)
                .item(item)
                .quantity(1)
                .status(UserItemStatus.OWNED)
                .enhancementGrade(enhancementGrade)
                .obtainedFrom(UserItemObtainedFrom.PICKUP)
                .build();

        ReflectionTestUtils.setField(userItem, "userItemId", userItemId);

        return userItem;
    }
}
