package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dao.ItemRequestService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemRequestDto> addItemRequest(@RequestHeader(SHARER_USER_ID) Long userId,
                                                         @RequestBody ItemRequestDto itemRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemRequestService.addItemRequest(userId, itemRequestDto));
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<ItemRequestDto>> getAllRequests(@RequestHeader(SHARER_USER_ID) Long userId) {
        return ResponseEntity.ok().body(itemRequestService.getAllRequests(userId));
    }

    @GetMapping
    public ResponseEntity<Collection<ItemRequestDto>> getAllUserRequests(@RequestHeader(SHARER_USER_ID) Long userId) {
        return ResponseEntity.ok().body(itemRequestService.getAllUserRequests(userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(@RequestHeader(SHARER_USER_ID) Long userId,
                                                         @PathVariable Long requestId) {
        return ResponseEntity.ok().body(itemRequestService.getRequestById(userId, requestId));
    }

}
