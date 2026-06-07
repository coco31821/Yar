package io.yar.yar2026.enhancement.service;

import io.yar.yar2026.enhancement.domain.EnhancementRule;
import io.yar.yar2026.enhancement.dto.EnhancementInfoResponse;
import io.yar.yar2026.enhancement.dto.MiracleTimeResponse;
import io.yar.yar2026.enhancement.exception.EnhancementMaxGradeException;
import io.yar.yar2026.enhancement.exception.EnhancementRuleNotFoundException;
import io.yar.yar2026.enhancement.exception.ItemNotEnhanceableException;
import io.yar.yar2026.enhancement.repository.EnhancementRuleRepository;
import io.yar.yar2026.enhancement.repository.MiracleTimeEventRepository;
import io.yar.yar2026.inventory.domain.UserItem;
import io.yar.yar2026.inventory.domain.UserItemStatus;
import io.yar.yar2026.inventory.exception.UserItemNotFoundException;
import io.yar.yar2026.inventory.repository.UserItemRepository;
import io.yar.yar2026.item.domain.ItemGrade;
import io.yar.yar2026.item.domain.ItemType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnhancementService {

    private final UserItemRepository userItemRepository;
    private final EnhancementRuleRepository enhancementRuleRepository;
    private final MiracleTimeEventRepository miracleTimeEventRepository;

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

        if (currentGrade >= maxGrade) {
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
}
