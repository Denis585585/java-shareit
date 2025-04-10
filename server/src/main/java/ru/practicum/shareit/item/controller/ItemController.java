package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dao.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader(SHARER_USER_ID) Long userId,
                                           @RequestBody ItemDto itemDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createItem(userId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@RequestHeader(SHARER_USER_ID) Long userId,
                                           @PathVariable Long itemId) {
        return ResponseEntity.ok().body(itemService.getItem(userId, itemId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(SHARER_USER_ID) Long userId,
                                              @PathVariable Long itemId,
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
    public ResponseEntity<Collection<ItemDto>> getAllItems(@RequestHeader(SHARER_USER_ID) Long userId) {
        return ResponseEntity.ok().body(itemService.getAllItems(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<ItemDto>> searchItems(@RequestParam(name = "text") String text) {
        return ResponseEntity.ok().body(itemService.searchItems(text));
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader(SHARER_USER_ID) Long userId,
                                                 @PathVariable  Long itemId,
                                                 @RequestBody  CommentNewDto commentDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.addComment(userId, itemId, commentDto));
    }
}
