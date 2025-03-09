package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@Service
public interface ItemService {

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getItem(Long userId, Long itemId);

    void deleteItem(Long userId, Long itemId);

    Collection<ItemDto> getAllItems(Long userId);

    Collection<ItemDto> searchItems(String text);

    CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto);
}
