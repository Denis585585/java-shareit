package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    Collection<ItemDto> getAllItems(Long userId);

    ItemDto getItem(Long userId, Long itemId);

    void deleteItem(Long userId, Long itemId);

    Collection<ItemDto> searchItems(Long userId, String text);


}
