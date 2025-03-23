package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentNewDto;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        User user = findUser(userId);
        Item item = itemMapper.toItem(itemDto, user);
        itemRepository.save(item);
        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        User user = findUser(userId);
        Item updatedItem = findItem(itemId);
        if (!updatedItem.getOwner().equals(user)) {
            throw new NotFoundException("Редактировать предмет может только его владелец");
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

        itemRepository.save(updatedItem);
        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        Item item = findItem(itemId);
        ItemDto itemDto = itemMapper.toItemDto(item);
        itemDto.setComments(commentRepository.findAllByItemId(itemId).stream()
                .map(commentMapper::toCommentDto)
                .toList());
        return itemDto;
    }

    @Override
    @Transactional
    public void deleteItem(Long userId, Long itemId) {
        Item item = findItem(itemId);
        itemRepository.delete(item);
    }

    @Override
    public Collection<ItemDto> getAllItems(Long userId) {
        List<Item> items = itemRepository.findByOwnerId(userId);
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();
        Map<Long, List<Comment>> comments = commentRepository.findAllByItemIdIn(itemIds)
                .stream()
                .collect(Collectors.groupingBy(o -> o.getItem().getId()));

        items.forEach(item -> item.setComments(comments.getOrDefault(item.getId(), List.of())));

        List<ItemDto> itemDtos = items.stream()
                .map(itemMapper::toItemDto)
                .toList();

        for (ItemDto itemDto : itemDtos) {
            setLastAndNextBooking(itemDto);
        }

        return itemDtos;
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<Item> items = itemRepository.findByRequest(text);
        return items.stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentNewDto commentDto) {
        User user = findUser(userId);
        Item item = findItem(itemId);

        Collection<Booking> bookings = bookingRepository.findAllByItemIdAndBookerId(itemId, userId);

        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.APPROVED && booking.getEnd().isBefore(LocalDateTime.now())) {
                Comment comment = commentMapper.toComment(commentDto, item, user);
                commentRepository.save(comment);
                return commentMapper.toCommentDto(comment);
            }
        }
        throw new RuntimeException("Нельзя оставить комментарий, если не бронировал предмет, " +
                "или бронирование не подтверждено");
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден"));
    }

    private Item findItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с таким id не найден"));
    }

    private void setLastAndNextBooking(ItemDto itemDto) {
        LocalDateTime lastBooking = null;
        LocalDateTime nextBooking = null;

        LocalDateTime now = LocalDateTime.now();
        Collection<Booking> bookings = bookingRepository.findAllByItemIdOrderByStartAsc(itemDto.getId());
        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.REJECTED) {
                return;
            }
            if (booking.getEnd().isBefore(now)) {
                lastBooking = booking.getStart();
            }

            if (booking.getStart().isAfter(now)) {
                nextBooking = booking.getEnd();
                break;
            }
        }

        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
    }
}
