package io.yar.yar2026.item;

import io.yar.yar2026.item.domain.Item;
import io.yar.yar2026.item.dto.ItemResponse;
import io.yar.yar2026.item.exception.ItemNotFoundException;
import io.yar.yar2026.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;


    @Transactional(readOnly = true)
    public ItemResponse getItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(ItemNotFoundException::new);

        return ItemResponse.from(item);
    }


}
