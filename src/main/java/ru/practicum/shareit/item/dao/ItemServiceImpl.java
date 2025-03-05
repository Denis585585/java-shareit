package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.save(itemMapper.toItem(itemDto, user));
        log.info("Item has been created");
        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with ID= " + itemId + " not found"));
        if (!updatedItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only its owner can edit an item");
        }
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(itemDto.getAvailable());
        }
        log.info("Item with ID= {} has been updated", itemId);
        return itemMapper.toItemDto(itemRepository.save(updatedItem));
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with ID= " + itemId + " not found"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Owner with ID= " + userId + " not found");
        }
        ItemDto itemDto = itemMapper.toItemDto(item);
        itemDto.setComments(commentRepository.findAllByItemId(itemId).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList()));
        return itemDto;
    }

    @Override
    @Transactional
    public void deleteItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with ID= " + itemId + " not found"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Only its owner can delete an item");
        }
        log.info("Item with ID= {} has been deleted", itemId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public Collection<ItemDto> getAllItems(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with ID= " + userId + " not found");
        }
        LocalDateTime date = LocalDateTime.now();
        Collection<Item> items = itemRepository.findAllItemsByUserId(userId);
        Collection<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();
        Map<Long, List<Comment>> comments = commentRepository.findAllByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        items.forEach(item -> item.setComments(comments.getOrDefault(item.getId(), List.of())));
        Collection<Booking> bookings = bookingRepository.findAllByItemId(itemIds, date);
        return items.stream()
                .map(item -> {
                    Booking lastBooking = bookings.stream()
                            .filter(booking -> booking.getItem().equals(item)
                                    && booking.getEndDate().isBefore(date))
                            .max(Comparator.comparing(Booking::getEndDate))
                            .orElse(null);
                    Booking nextBooking = bookings.stream()
                            .filter(booking -> booking.getItem().equals(item)
                                    && booking.getStartDate().isAfter(date))
                            .min(Comparator.comparing(Booking::getStartDate))
                            .orElse(null);
                    LocalDateTime lastBookingDate = lastBooking != null ? lastBooking.getEndDate() : null;
                    LocalDateTime nextBookingDate = nextBooking != null ? nextBooking.getStartDate() : null;
                    return itemMapper.toItemDtoDate(item, nextBookingDate, lastBookingDate);
                }).collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItems(Long userId, String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItemByDescription(userId, text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with ID= " + itemId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Collection<Booking> bookings = bookingRepository.findAllByItemAndBookerId(itemId, userId);

        for (Booking booking : bookings) {
            if (booking.getBookingStatus() == BookingStatus.APPROVED
                    && booking.getEndDate().isBefore(LocalDateTime.now())) {
                Comment comment = commentMapper.toComment(commentDto.getText(), item, user);
                commentRepository.save(comment);
                return commentMapper.toCommentDto(comment);
            }
        }

        throw new RuntimeException("Нельзя оставить комментарий, если не бронировал предмет," +
                " или бронирование не подтверждено");
    }
}
