package ru.practicum.shareit.booking.dao;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.util.BookingState;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long bookerId, BookingNewDto bookingDto);

    BookingDto updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingByOwnerId(Long userId, Long bookingId);

    List<BookingDto> getAllBookingsByState(Long userId, BookingState state);

    List<BookingDto> getBookingsStateByOwner(Long userId, BookingState state);
}
