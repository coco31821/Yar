package io.yar.yar2026.item.repository;

import io.yar.yar2026.item.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
