package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findAllItemsByUserId(Long userId);

    @Query("SELECT i FROM Item i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', ?2, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?2, '%'))) " +
            "AND i.owner.id = ?1 AND i.available = true")
    Collection<Item> searchItemByDescription(Long userId, String text);
}
