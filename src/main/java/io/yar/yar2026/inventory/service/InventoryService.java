package io.yar.yar2026.inventory.service;

import io.yar.yar2026.inventory.domain.UserItem;
import io.yar.yar2026.inventory.domain.UserItemObtainedFrom;
import io.yar.yar2026.inventory.domain.UserItemStatus;
import io.yar.yar2026.inventory.dto.ItemPickupRequest;
import io.yar.yar2026.inventory.dto.UserItemResponse;
import io.yar.yar2026.inventory.repository.UserItemRepository;
import io.yar.yar2026.item.domain.Item;
import io.yar.yar2026.item.exception.ItemNotFoundException;
import io.yar.yar2026.item.repository.ItemRepository;
import io.yar.yar2026.user.domain.User;
import io.yar.yar2026.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;

    @Transactional
    public UserItemResponse pickup(Long userId, ItemPickupRequest request) {
        User user = userService.requireExists(userId);

        Item item = itemRepository.findById(request.itemId())
                .orElseThrow(ItemNotFoundException::new);

        UserItem userItem = UserItem.builder()
                .user(user)
                .item(item)
                .quantity(request.quantity())
                .status(UserItemStatus.OWNED)
                .enhancementGrade(0)
                .obtainedFrom(UserItemObtainedFrom.PICKUP)  // 획득경로 정리
                .build();

        UserItem savedUserItem = userItemRepository.save(userItem);

        return UserItemResponse.from(savedUserItem);
    }
}