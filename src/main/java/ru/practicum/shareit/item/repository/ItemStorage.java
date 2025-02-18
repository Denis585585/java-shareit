package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item addNewItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    Collection<Item> getAllItems(Long userId);

    Item getItem(Long userId, Long itemId);

    void deleteItem(Long userId, Long itemId);

    Collection<Item> searchItems(Long userId, String text);
}
