package ru.practicum.shareit.booking.contoller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dao.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.util.BookingState;

import java.util.Collection;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private static final String SHARER_USER_ID = "X-Sharer-User-Id";


    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestHeader(SHARER_USER_ID) Long userId,
                                                    @Valid @RequestBody BookingDto bookingDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(userId, bookingDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approvedBooking(@RequestHeader(SHARER_USER_ID) Long userId,
                                                      @PathVariable Long bookingId,
                                                      @RequestParam Boolean approved) {
        return ResponseEntity.ok().body(bookingService.approvedBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@RequestHeader(SHARER_USER_ID) Long userId,
                                                 @PathVariable Long bookingId) {
        return ResponseEntity.ok().body(bookingService.getBooking(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<Collection<BookingDto>> getAllBookingByState(
            @RequestHeader(SHARER_USER_ID) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return ResponseEntity.ok().body(bookingService.getAllBookingStateByUser(userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<Collection<BookingDto>> getBookingsByOwnerId(
            @RequestHeader(SHARER_USER_ID) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state) {
        return ResponseEntity.ok().body(bookingService.getBookingsStateByOwner(userId, state));
    }


}
