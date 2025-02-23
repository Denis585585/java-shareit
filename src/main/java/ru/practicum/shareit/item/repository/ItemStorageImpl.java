package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;
    private final UserStorage userStorage;

    @Override
    public Item createItem(Long userId, Item item) {
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("The user with id= " + userId + " not fount in the database.");
        }
        item.setOwner(userStorage.getUser(userId));
        if ((item.getName() == null || item.getName().isBlank())
                || (item.getDescription() == null || item.getDescription().isBlank())) {
            throw new IllegalArgumentException("Fields name and description cannot be empty");
        }
        item.setId(id);
        items.put(item.getId(), item);
        id++;
        return item;
    }

    @Override
    public Item getItem(Long userId, Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("The item with id= " + itemId + " not fount in the database.");
        }
        if (!items.get(itemId).getOwner().getId().equals(userId)) {
            throw new NotFoundException("There is no user with this ID= " + userId);
        }
        return items.get(itemId);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("The item with id= " + itemId + " not fount in the database.");
        }
        if (!items.get(itemId).getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only its owner can edit an item");
        }
        Item updatedItem = items.get(itemId);
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        items.put(itemId, updatedItem);
        return item;
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("The item with id= " + itemId + " not fount in the database.");
        }
        if (!items.get(itemId).getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only its owner can delete an item");
        }
        items.remove(itemId);
    }

    @Override
    public Collection<Item> getAllItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .toList();
    }

    @Override
    public Collection<Item> searchItems(Long userId, String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String textToLowerCase = text.toLowerCase();
        return items.values().stream()
                .filter(item -> (item.getDescription().toLowerCase().contains(textToLowerCase)
                        || item.getName().toLowerCase().contains(textToLowerCase))
                        && item.getOwner().getId().equals(userId)
                        && item.getAvailable().booleanValue())
                .toList();
    }
}
