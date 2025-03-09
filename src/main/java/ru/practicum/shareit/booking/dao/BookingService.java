package ru.practicum.shareit.booking.dao;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.util.BookingState;

import java.util.Collection;

@Service
public interface BookingService {

    BookingDto createBooking(Long userId, BookingNewDto bookingDto);

    BookingDto approvedBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingByOwnerId(Long userId, Long bookingId);

    Collection<BookingDto> getAllBookingsByState(Long userId, BookingState state);

    Collection<BookingDto> getBookingsStateByOwner(Long userId, BookingState state);
}
