package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dao.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    @Autowired
    private final ItemService itemService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader(SHARER_USER_ID) Long userId,
                                           @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(itemService.createItem(userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@RequestHeader(SHARER_USER_ID) Long userId,
                                           @PathVariable Long itemId) {
        return ResponseEntity.ok().body(itemService.getItem(userId, itemId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(SHARER_USER_ID) Long userId,
                                              @PathVariable @Valid Long itemId,
                                              @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(itemService.updateItem(userId, itemId, itemDto));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@RequestHeader(SHARER_USER_ID) Long userId,
                                           @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems(@RequestHeader(SHARER_USER_ID) Long userId) {
        return ResponseEntity.ok().body(itemService.getAllItems(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestHeader(SHARER_USER_ID) Long userId,
                                                     @RequestParam(required = false) String text) {
        return ResponseEntity.ok().body(itemService.searchItems(userId, text));
    }
}
