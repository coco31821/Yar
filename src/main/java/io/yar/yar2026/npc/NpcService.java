package io.yar.yar2026.npc;

import io.yar.yar2026.inventory.domain.UserItem;
import io.yar.yar2026.inventory.domain.UserItemObtainedFrom;
import io.yar.yar2026.inventory.domain.UserItemStatus;
import io.yar.yar2026.inventory.repository.UserItemRepository;
import io.yar.yar2026.item.domain.Item;
import io.yar.yar2026.item.domain.ItemType;
import io.yar.yar2026.npc.domain.Npc;
import io.yar.yar2026.npc.domain.NpcSaleItem;
import io.yar.yar2026.npc.dto.NpcPurchaseRequest;
import io.yar.yar2026.npc.dto.NpcPurchaseResponse;
import io.yar.yar2026.npc.dto.NpcResponse;
import io.yar.yar2026.npc.dto.NpcShopItemResponse;
import io.yar.yar2026.npc.exception.NpcNotFoundException;
import io.yar.yar2026.npc.exception.ShopItemNotFoundException;
import io.yar.yar2026.npc.repository.NpcRepository;
import io.yar.yar2026.npc.repository.NpcSaleItemRepository;
import io.yar.yar2026.user.domain.User;
import io.yar.yar2026.user.service.UserService;
import io.yar.yar2026.wallet.domain.Wallet;
import io.yar.yar2026.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NpcService {

    private final NpcRepository npcRepository;
    private final NpcSaleItemRepository npcSaleItemRepository;
    private final UserService userService;
    private final WalletRepository walletRepository;
    private final UserItemRepository userItemRepository;

    public NpcResponse getNpc(Long npcId) {
        Npc npc = npcRepository.findByNpcIdAndActiveTrue(npcId)
                .orElseThrow(NpcNotFoundException::new);

        List<NpcShopItemResponse> shopItems = npcSaleItemRepository
                .findAllByNpc_NpcIdAndActiveTrueOrderBySortOrderAsc(npcId)
                .stream()
                .map(NpcShopItemResponse::from)
                .toList();

        return NpcResponse.from(npc, shopItems);
    }

    public List<NpcResponse> getNpcs() {
        List<Npc> npcs = npcRepository.findByActiveTrue();
        List<Long> npcIds = npcs.stream()
                .map(Npc::getNpcId)
                .toList();

        if (npcIds.isEmpty()) {
            return List.of();
        }

        Map<Long, List<NpcShopItemResponse>> shopItemsByNpcId = npcSaleItemRepository
                .findAllByNpc_NpcIdInAndActiveTrueOrderBySortOrderAsc(npcIds)
                .stream()
                .collect(Collectors.groupingBy(
                        npcSaleItem -> npcSaleItem.getNpc().getNpcId(),
                        Collectors.mapping(NpcShopItemResponse::from, Collectors.toList())
                ));

        return npcs.stream()
                .map(npc -> NpcResponse.from(
                        npc,
                        shopItemsByNpcId.getOrDefault(npc.getNpcId(), List.of())
                ))
                .toList();
    }

    @Transactional
    public NpcPurchaseResponse purchase(
            Long userId,
            Long npcId,
            Long npcItemId,
            NpcPurchaseRequest request
    ) {
        User user = userService.requireExists(userId);
        int quantity = request == null ? 1 : request.purchaseQuantity();

        NpcSaleItem npcSaleItem = npcSaleItemRepository
                .findByNpc_NpcIdAndNpcSaleItemIdAndActiveTrue(npcId, npcItemId)
                .filter(saleItem -> saleItem.isOnSale(LocalDateTime.now()))
                .orElseThrow(ShopItemNotFoundException::new);

        int goldSpent = npcSaleItem.getPrice() * quantity;
        Wallet wallet = walletRepository.findByUser_UserId(userId)
                .orElseGet(() -> walletRepository.save(Wallet.createDefault(user)));

        wallet.useGold(goldSpent);
        npcSaleItem.decreaseStock(quantity);

        List<UserItem> userItems = addPurchasedItems(user, npcSaleItem.getItem(), quantity);

        return NpcPurchaseResponse.of(
                wallet,
                userItems
        );
    }

    private List<UserItem> addPurchasedItems(User user, Item item, int quantity) {
        if (item.getItemType() == ItemType.CONSUMABLE) {
            UserItem userItem = userItemRepository
                    .findByUser_UserIdAndItem_ItemIdAndEnhancementGradeAndStatus(
                            user.getUserId(),
                            item.getItemId(),
                            0,
                            UserItemStatus.OWNED
                    )
                    .map(existingUserItem -> {
                        existingUserItem.increaseQuantity(quantity);
                        return existingUserItem;
                    })
                    .orElseGet(() -> userItemRepository.save(
                            createPurchasedUserItem(user, item, quantity)
                    ));

            return List.of(userItem);
        }

        List<UserItem> userItems = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            userItems.add(userItemRepository.save(createPurchasedUserItem(user, item, 1)));
        }

        return userItems;
    }

    private UserItem createPurchasedUserItem(User user, Item item, int quantity) {
        return UserItem.builder()
                .user(user)
                .item(item)
                .quantity(quantity)
                .status(UserItemStatus.OWNED)
                .enhancementGrade(0)
                .obtainedFrom(UserItemObtainedFrom.NPC_SHOP)
                .build();
    }
}
