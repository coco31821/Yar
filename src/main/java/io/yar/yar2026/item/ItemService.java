package io.yar.yar2026.item;

import io.yar.yar2026.item.domain.Item;
import io.yar.yar2026.item.dto.ItemResponse;
import io.yar.yar2026.item.exception.ItemNotFoundException;
import io.yar.yar2026.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;


    // 아이템 단건 조회
    @Transactional(readOnly = true)
    public ItemResponse getItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(ItemNotFoundException::new);

        return ItemResponse.from(item);
    }

    // 아이템 목록 전체 조회
    public List<ItemResponse> getItems() {
        return itemRepository.findAll()
                .stream()
                .map(ItemResponse::from)
                .toList();

    }


}
