package io.yar.yar2026.inventory.repository;

import io.yar.yar2026.inventory.domain.UserItem;
import io.yar.yar2026.inventory.domain.UserItemStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {

    @EntityGraph(attributePaths = {"item"})
    List<UserItem> findAllByUser_UserIdAndStatusIn(Long userId, Collection<UserItemStatus> statuses);

    // 같은 묶음 row가 있는지 찾는 메서드
    Optional<UserItem> findByUser_UserIdAndItem_ItemIdAndEnhancementGradeAndStatus(
            Long userId,
            Long itemId,
            int enhancementGrade,
            UserItemStatus status
    );
}
