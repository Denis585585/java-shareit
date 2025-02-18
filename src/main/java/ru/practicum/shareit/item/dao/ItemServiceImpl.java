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

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        return ItemMapper.toItemDto(
                itemStorage.addNewItem(userId, ItemMapper.toItem(itemDto, userStorage.getUser(userId))));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        return ItemMapper.toItemDto(
                itemStorage.updateItem(itemId, userId, ItemMapper.toItem(itemDto, userStorage.getUser(userId))));
    }

    @Override
    public Collection<ItemDto> getAllItems(Long userId) {
        return itemStorage.getAllItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        return ItemMapper.toItemDto(itemStorage.getItem(userId, itemId));
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        itemStorage.deleteItem(userId, itemId);
    }

    @Override
    public Collection<ItemDto> searchItems(Long userId, String text) {
        return itemStorage.searchItems(userId, text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
