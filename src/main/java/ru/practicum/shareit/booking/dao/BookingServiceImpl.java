package ru.practicum.shareit.booking.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingDto bookingDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID= " + userId + " not found"));
        Item item = itemRepository.findById(bookingDto.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Item with ID= "
                        + bookingDto.getItem().getId() + " not found"));
        if (!item.getAvailable()) {
            throw new RuntimeException("The item has already been reserved by someone");
        }
        Collection<Booking> bookings = bookingRepository.findAllRequaredDates(bookingDto.getItem().getId(),
                Set.of(BookingStatus.APPROVED), bookingDto.getStartDate(), bookingDto.getEndDate());
        if (!bookings.isEmpty()) {
            throw new NotFoundException("Item is occupied on the specified dates");
        }

        Booking booking = bookingRepository.save(bookingMapper
                .toBooking(bookingDto, item, user, BookingStatus.WAITING));
        log.info("Booking has been created");
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approvedBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with ID= " + bookingId + " not found"));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Item with ID= "
                        + booking.getItem().getId() + " not found"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new RuntimeException("User with ID= " + userId + " be not owner");
        }
        if (booking.getBookingStatus() != BookingStatus.WAITING) {
            throw new RuntimeException("Booking already confirmed");
        }
        if (approved) {
            booking.setBookingStatus(BookingStatus.APPROVED);
        } else {
            booking.setBookingStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        return bookingRepository.findById(bookingId)
                .filter(booking -> booking.getBooker().getId().equals(userId)
                        || booking.getItem().getOwner().getId().equals(userId))
                .map(bookingMapper::toBookingDto)
                .orElseThrow(() -> new NotFoundException("Booking with ID= " + bookingId + " not found"));
    }

    @Override
    public Collection<BookingDto> getAllBookingStateByUser(Long userId, BookingState state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID= " + userId + " not found"));
        Collection<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBookerOrderByStartDesc(user);
            case CURRENT -> bookingRepository.findAllByBookerAndStatusAndEndIsAfterOrderByStartDesc(user,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case PAST -> bookingRepository.findAllByBookerAndStatusAndEndIsBeforeOrderByStartDesc(user,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByBookerAndStatusAndStartIsAfterOrderByStartDesc(user,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByBookerAndStatusOrderByStartDesc(user, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerAndStatusOrderByStartDesc(user, BookingStatus.REJECTED);
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID= " + userId + " not found"));
        Collection<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByItemOwnerOrderByStartDesc(user);
            case CURRENT -> bookingRepository.findAllByItemOwnerAndStatusAndEndIsAfterOrderByStartDesc(user,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case PAST -> bookingRepository.findAllByItemOwnerAndStatusAndEndIsBeforeOrderByStartDesc(user,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case FUTURE -> bookingRepository.findAllByItemOwnerAndStatusAndStartIsAfterOrderByStartDesc(user,
                    BookingStatus.APPROVED, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(user, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(user, BookingStatus.REJECTED);
        };
        if (bookings.isEmpty()) {
            return List.of();
        } else {
            return bookings.stream()
                    .map(bookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
    }
}
