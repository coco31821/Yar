package io.yar.yar2026.enhancement.service;

import io.yar.yar2026.enhancement.domain.EnhancementOutcome;
import io.yar.yar2026.enhancement.domain.EnhancementRule;
import io.yar.yar2026.enhancement.domain.ItemEnhancementHistory;
import io.yar.yar2026.enhancement.domain.MiracleTimeEvent;
import io.yar.yar2026.enhancement.dto.EnhancementInfoResponse;
import io.yar.yar2026.enhancement.dto.EnhancementResultResponse;
import io.yar.yar2026.enhancement.dto.MiracleTimeResponse;
import io.yar.yar2026.enhancement.exception.EnhancementMaxGradeException;
import io.yar.yar2026.enhancement.exception.EnhancementRuleNotFoundException;
import io.yar.yar2026.enhancement.exception.ItemNotEnhanceableException;
import io.yar.yar2026.enhancement.repository.ItemEnhancementHistoryRepository;
import io.yar.yar2026.enhancement.repository.EnhancementRuleRepository;
import io.yar.yar2026.enhancement.repository.MiracleTimeEventRepository;
import io.yar.yar2026.inventory.domain.UserItem;
import io.yar.yar2026.inventory.domain.UserItemStatus;
import io.yar.yar2026.inventory.dto.UserItemResponse;
import io.yar.yar2026.inventory.exception.UserItemNotFoundException;
import io.yar.yar2026.inventory.repository.UserItemRepository;
import io.yar.yar2026.item.domain.ItemGrade;
import io.yar.yar2026.item.domain.ItemType;
import io.yar.yar2026.user.exception.UserNotFoundException;
import io.yar.yar2026.wallet.domain.Wallet;
import io.yar.yar2026.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnhancementService {

    private final UserItemRepository userItemRepository;
    private final EnhancementRuleRepository enhancementRuleRepository;
    private final MiracleTimeEventRepository miracleTimeEventRepository;
    private final WalletRepository walletRepository;
    private final ItemEnhancementHistoryRepository itemEnhancementHistoryRepository;

    public EnhancementInfoResponse getEnhancementInfo(Long userId, Long userItemId) {
        UserItem userItem = userItemRepository
                .findByUserItemIdAndUser_UserIdAndStatusIn(
                        userItemId,
                        userId,
                        List.of(UserItemStatus.OWNED, UserItemStatus.EQUIPPED)
                )
                .orElseThrow(UserItemNotFoundException::new);

        validateEnhanceable(userItem);

        int currentGrade = userItem.getEnhancementGrade();
        int maxGrade = getMaxGrade(userItem.getItem().getItemGrade());

        if (currentGrade > maxGrade) {
            throw new EnhancementMaxGradeException();
        }

        EnhancementRule rule = enhancementRuleRepository
                .findByFromGradeAndActiveTrue(currentGrade)
                .orElseThrow(EnhancementRuleNotFoundException::new);

        LocalDateTime now = LocalDateTime.now();
        boolean miracleTime = miracleTimeEventRepository
                .findActiveEvent(now)
                .isPresent();

        return new EnhancementInfoResponse(
                userItem.getUserItemId(),
                currentGrade,
                maxGrade,
                false,
                rule.getSuccessRate(),
                rule.getFailRate(),
                rule.getDestroyRate(),
                rule.getGoldCost(),
                miracleTime
        );
    }

    private void validateEnhanceable(UserItem userItem) {
        ItemType itemType = userItem.getItem().getItemType();

        if (itemType != ItemType.WEAPON && itemType != ItemType.ARMOR) {
            throw new ItemNotEnhanceableException();
        }
    }

    private int getMaxGrade(ItemGrade itemGrade) {
        return switch (itemGrade) {
            case COMMON -> 5;
            case UNCOMMON -> 7;
            case RARE -> 10;
            case EPIC -> 15;
            case LEGENDARY -> 20;
        };
    }

    // 미라클 타임
    public MiracleTimeResponse getMiracleTime() {
        LocalDateTime now = LocalDateTime.now();

        return miracleTimeEventRepository.findActiveEvent(now)
                .map(event -> new MiracleTimeResponse(
                        true,
                        event.getNoticeMessage()
                ))
                .orElseGet(() -> new MiracleTimeResponse(
                        false,
                        "현재 진행 중인 미라클 타임이 없습니다."
                ));
    }

    // 강화 실행
    @Transactional
    public EnhancementResultResponse enhance(Long userId, Long userItemId) {
        UserItem userItem = getUserItem(userId, userItemId);

        validateEnhanceable(userItem);

        int gradeBefore = userItem.getEnhancementGrade();
        int maxGrade = getMaxGrade(userItem.getItem().getItemGrade());

        if (gradeBefore >= maxGrade) {
            throw new EnhancementMaxGradeException();
        }

        EnhancementRule rule = getEnhancementRule(gradeBefore);
        Wallet wallet = getWallet(userId);

        wallet.useGold(rule.getGoldCost());

        Optional<MiracleTimeEvent> miracleTimeEvent = getActiveMiracleTime();

        EnhancementOutcome outcome = decideOutcome(rule);

        EnhancementApplyResult applyResult = applyEnhancementResult(
                userItem,
                gradeBefore,
                maxGrade,
                miracleTimeEvent.isPresent(),
                outcome
        );

        saveHistory(
                applyResult.userItem(),
                rule,
                gradeBefore,
                applyResult.gradeAfter(),
                outcome,
                miracleTimeEvent.orElse(null)
        );

        return new EnhancementResultResponse(
                outcome.name(),
                miracleTimeEvent.isPresent(),
                gradeBefore,
                applyResult.gradeAfter(),
                rule.getGoldCost(),
                wallet.getGold(),
                UserItemResponse.from(applyResult.userItem())
        );
    }

    // 아이템 조회 메서드
    private UserItem getUserItem(Long userId, Long userItemId) {
        return userItemRepository
                .findByUserItemIdAndUser_UserIdAndStatusIn(
                        userItemId,
                        userId,
                        List.of(UserItemStatus.OWNED, UserItemStatus.EQUIPPED)
                )
                .orElseThrow(UserItemNotFoundException::new);
    }


    // 강화 규칙 조회 메서드
    private EnhancementRule getEnhancementRule(int currentGrade) {
        return enhancementRuleRepository
                .findByFromGradeAndActiveTrue(currentGrade)
                .orElseThrow(EnhancementRuleNotFoundException::new);
    }

    //지갑 조회
    private Wallet getWallet(Long userId) {
        return walletRepository.findByUser_UserId(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    // 미라클 타임 조회
    private Optional<MiracleTimeEvent> getActiveMiracleTime() {
        return miracleTimeEventRepository.findActiveEvent(LocalDateTime.now());
    }

    // 결과 결정
    private EnhancementOutcome decideOutcome(EnhancementRule rule) {
        int successRoll = rollPercent();

        if (successRoll <= rule.getSuccessRate()) {
            return EnhancementOutcome.SUCCESS;
        }

        int destroyRoll = rollPercent();

        if (destroyRoll <= rule.getDestroyRate()) {
            return EnhancementOutcome.DESTROYED;
        }

        return EnhancementOutcome.FAIL;
    }

    private int rollPercent() {
        return ThreadLocalRandom.current().nextInt(1, 101);
    }

    // 결과 적용
    private EnhancementApplyResult applyEnhancementResult(
            UserItem userItem,
            int gradeBefore,
            int maxGrade,
            boolean miracleTimeApplied,
            EnhancementOutcome outcome
    ) {
        if (outcome == EnhancementOutcome.SUCCESS) {
            int gradeStep = miracleTimeApplied ? 2 : 1;
            int gradeAfter = Math.min(gradeBefore + gradeStep, maxGrade);
            UserItem enhancedUserItem = moveOneItemToGrade(userItem, gradeAfter);

            return new EnhancementApplyResult(enhancedUserItem, gradeAfter);
        }

        if (outcome == EnhancementOutcome.DESTROYED) {
            userItem.decreaseQuantity(1);

            if (userItem.getQuantity() == 0) {
                userItem.destroy();
            }
        }

        return new EnhancementApplyResult(userItem, gradeBefore);
    }

    private UserItem moveOneItemToGrade(UserItem sourceUserItem, int gradeAfter) {
        if (sourceUserItem.getQuantity() == 1) {
            sourceUserItem.enhanceTo(gradeAfter);
            return sourceUserItem;
        }

        sourceUserItem.decreaseQuantity(1);

        return userItemRepository
                .findByUser_UserIdAndItem_ItemIdAndEnhancementGradeAndStatus(
                        sourceUserItem.getUser().getUserId(),
                        sourceUserItem.getItem().getItemId(),
                        gradeAfter,
                        sourceUserItem.getStatus()
                )
                .map(existingUserItem -> {
                    existingUserItem.increaseQuantity(1);
                    return existingUserItem;
                })
                .orElseGet(() -> userItemRepository.save(
                        UserItem.builder()
                                .user(sourceUserItem.getUser())
                                .item(sourceUserItem.getItem())
                                .quantity(1)
                                .status(sourceUserItem.getStatus())
                                .enhancementGrade(gradeAfter)
                                .obtainedFrom(sourceUserItem.getObtainedFrom())
                                .build()
                ));
    }

    //이력저장
    private void saveHistory(
            UserItem userItem,
            EnhancementRule rule,
            int gradeBefore,
            int gradeAfter,
            EnhancementOutcome outcome,
            MiracleTimeEvent miracleTimeEvent
    ) {
        itemEnhancementHistoryRepository.save(
                ItemEnhancementHistory.builder()
                        .user(userItem.getUser())
                        .userItem(userItem)
                        .item(userItem.getItem())
                        .beforeGrade(gradeBefore)
                        .afterGrade(gradeAfter)
                        .outcome(outcome)
                        .successRate(rule.getSuccessRate())
                        .failRate(rule.getFailRate())
                        .destroyRate(rule.getDestroyRate())
                        .goldSpent(rule.getGoldCost())
                        .miracleApplied(miracleTimeEvent != null)
                        .miracleTimeEvent(miracleTimeEvent)
                        .build()
        );
    }

    private record EnhancementApplyResult(
            UserItem userItem,
            int gradeAfter
    ) {
    }
}
