package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dao.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemService itemService;
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemDto> addNewItem(@RequestHeader(HEADER_USER_ID) Long userId,
                                              @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.addNewItem(userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@RequestHeader(HEADER_USER_ID) Long userId,
                                           @PathVariable Long itemId) {
        return ResponseEntity.ok().body(itemService.getItem(userId, itemId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(HEADER_USER_ID) Long userId,
                                              @PathVariable @Valid Long itemId,
                                              @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok().body(itemService.updateItem(userId, itemId, itemDto));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@RequestHeader(HEADER_USER_ID) Long userId,
                                           @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> getAllItems(@RequestHeader(HEADER_USER_ID) Long userId) {
        return ResponseEntity.ok().body(itemService.getAllItems(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> searchItems(@RequestHeader(HEADER_USER_ID) Long userId,
                                                           @RequestParam(required = false) String text) {
        return ResponseEntity.ok().body(itemService.searchItems(userId, text));
    }
}
