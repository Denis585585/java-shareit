package ru.practicum.shareit.booking.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.util.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingNewDto bookingDto) {
        User booker = getUser(userId);
        Item item = getItem(bookingDto.getItemId());
        if (!item.getAvailable()) {
            throw new RuntimeException("The item has already been reserved by someone");
        }
        Collection<Booking> bookings = bookingRepository.findAllWithIntersectionDates(bookingDto.getItemId(),
                Set.of(BookingStatus.APPROVED), bookingDto.getStart(), bookingDto.getEnd());
        if (!bookings.isEmpty()) {
            throw new NotFoundException("Item is occupied on the specified dates");
        }

        Booking booking = bookingMapper.toBooking(bookingDto, item, booker, BookingStatus.WAITING);
        log.info("Booking has been created");
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approvedBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBooking(bookingId);
        Item item = getItem(booking.getItem().getId());
        if (!item.getOwner().getId().equals(userId)) {
            throw new RuntimeException("User with ID= " + userId + " be not owner");
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
    public BookingDto getBookingByOwnerId(Long ownerId, Long bookingId) {
        return bookingRepository.findById(bookingId)
                .filter(booking -> booking.getBooker().getId().equals(ownerId)
                        || booking.getItem().getOwner().getId().equals(ownerId))
                .map(bookingMapper::toBookingDto)
                .orElseThrow(() -> new NotFoundException("Booking with ID= " + bookingId + " not found"));
    }

    @Override
    public Collection<BookingDto> getAllBookingsByState(Long userId, BookingState state) {
        User user = getUser(userId);
        Collection<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId());
            case CURRENT -> bookingRepository.findAllByBookerIdAndStatusAndEndIsAfterOrderByStartDesc(userId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case PAST -> bookingRepository.findAllByBookerIdAndStatusAndEndIsBeforeOrderByStartDesc(userId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByBookerIdAndStatusAndStartIsAfterOrderByStartDesc(userId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };
        if (bookings.isEmpty()) {
            return List.of();
        } else {
            return bookings.stream()
                    .map(bookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Collection<BookingDto> getBookingsStateByOwner(Long userId, BookingState state) {
        User user = getUser(userId);
        Collection<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(user.getId());
            case CURRENT -> bookingRepository.findAllByItemOwnerIdAndStatusAndEndIsAfterOrderByStartDesc(userId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case PAST -> bookingRepository.findAllByItemOwnerIdAndStatusAndEndIsBeforeOrderByStartDesc(userId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByItemOwnerIdAndStatusAndStartIsAfterOrderByStartDesc(userId,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case WAITING ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };
        if (bookings.isEmpty()) {
            return List.of();
        } else {
            return bookings.stream()
                    .map(bookingMapper::toBookingDto)
                    .collect(Collectors.toList());
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
