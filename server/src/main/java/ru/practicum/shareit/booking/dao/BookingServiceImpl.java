package ru.practicum.shareit.booking.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingState;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto createBooking(Long bookerId, BookingNewDto bookingDto) {
        User booker = getUser(bookerId);
        UserDto bookerDto = userMapper.toUserDto(booker);
        Item item = getItem(bookingDto.getItemId());
        if (!item.getAvailable()) {
            throw new RuntimeException("The item has already been reserved by someone");
        }
        ItemDto itemDto = itemMapper.toItemDto(item);
        Collection<Booking> bookings = bookingRepository.findAllWithIntersectionDates(bookingDto.getItemId(),
                Set.of(BookingStatus.APPROVED), bookingDto.getStart(), bookingDto.getEnd());
        if (!bookings.isEmpty()) {
            throw new NotFoundException("Item is occupied on the specified dates");
        }

        Booking booking = bookingMapper.toBooking(bookingDto, itemDto, bookerDto, BookingStatus.WAITING);
        log.info("Booking has been created");
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = getBooking(bookingId);
        Item item = getItem(booking.getItem().getId());
        if (!item.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("User with ID= " + ownerId + " be not owner");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new RuntimeException("Booking already confirmed");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingByOwnerId(Long bookerId, Long bookingId) {
        return bookingRepository.findById(bookingId)
                .filter(booking -> booking.getBooker().getId().equals(bookerId)
                        || booking.getItem().getOwner().getId().equals(bookerId))
                .map(bookingMapper::toBookingDto)
                .orElseThrow(() -> new NotFoundException("Booking with ID= " + bookingId + " not found"));
    }

    @Override
    public List<BookingDto> getAllBookingsByState(Long bookerId, BookingState state) {
        User user = getUser(bookerId);
        Collection<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId());
            case CURRENT -> bookingRepository.findAllByBookerIdAndStatusAndEndIsAfterOrderByStartDesc(bookerId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case PAST -> bookingRepository.findAllByBookerIdAndStatusAndEndIsBeforeOrderByStartDesc(bookerId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByBookerIdAndStatusAndStartIsAfterOrderByStartDesc(bookerId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId,
                    BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId,
                    BookingStatus.REJECTED);
        };
        if (bookings.isEmpty()) {
            return List.of();
        } else {
            return bookings.stream().map(bookingMapper::toBookingDto).toList();
        }
    }

    @Override
    public List<BookingDto> getBookingsStateByOwner(Long ownerId, BookingState state) {
        User user = getUser(ownerId);
        Collection<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(user.getId());
            case CURRENT -> bookingRepository.findAllByItemOwnerIdAndStatusAndEndIsAfterOrderByStartDesc(ownerId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case PAST -> bookingRepository.findAllByItemOwnerIdAndStatusAndEndIsBeforeOrderByStartDesc(ownerId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByItemOwnerIdAndStatusAndStartIsAfterOrderByStartDesc(ownerId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId,
                    BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId,
                    BookingStatus.REJECTED);
        };
        if (bookings.isEmpty()) {
            return List.of();
        } else {
            return bookings.stream().map(bookingMapper::toBookingDto).toList();
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID= " + userId + " not found"));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with ID= " + itemId + " not found"));
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with ID= " + bookingId + " not found"));
    }
}
