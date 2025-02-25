package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        return itemMapper.toItemDto(
                itemStorage.createItem(userId, itemMapper.toItem(itemDto, userStorage.getUser(userId))));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        return itemMapper.toItemDto(
                itemStorage.updateItem(userId, itemId, itemMapper.toItem(itemDto, userStorage.getUser(userId))));
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        return itemMapper.toItemDto(itemStorage.getItem(userId, itemId));
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemStorage.deleteItem(userId, itemId);
    }

    @Override
    public Collection<ItemDto> getAllItems(Long userId) {
        return itemStorage.getAllItems(userId).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> searchItems(Long userId, String text) {
        return itemStorage.searchItems(userId, text).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }
}
