package ru.practicum.shareit.booking.dao;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.util.BookingState;

import java.util.Collection;

public interface BookingService {

    BookingDto createBooking(Long userId, BookingDto bookingDto);

    BookingDto approvedBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getBooking(Long userId, Long bookingId);

    Collection<BookingDto> getAllBookingStateByUser(Long userId, BookingState state);

    Collection<BookingDto> getBookingsStateByOwner(Long userId, BookingState state);
}
