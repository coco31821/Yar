package io.yar.yar2026.inventory.repository;

import io.yar.yar2026.inventory.domain.UserItem;
import io.yar.yar2026.inventory.domain.UserItemStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {

    @EntityGraph(attributePaths = {"item"})
    List<UserItem> findAllByUser_UserIdAndStatusIn(Long userId, Collection<UserItemStatus> statuses);
}
