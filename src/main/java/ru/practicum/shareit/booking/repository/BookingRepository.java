package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b from Booking as b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status in (:statuses) " +
            "AND :startDate <= b.end " +
            "AND :endDate >= b.start")
    Collection<Booking> findAllWithIntersectionDates(Long itemId, Set<BookingStatus> statuses, LocalDateTime startDate,
                                                     LocalDateTime endDate);

    Collection<Booking> findAllByItemIdAndBookerId(Long itemId, Long bookerId);

    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    Collection<Booking> findAllByBookerIdAndStatusAndEndIsAfterOrderByStartDesc(Long bookerId,
                                                                                BookingStatus status, LocalDateTime dt);

    Collection<Booking> findAllByBookerIdAndStatusAndEndIsBeforeOrderByStartDesc(Long bookerId,
                                                                                 BookingStatus status, LocalDateTime dt);

    Collection<Booking> findAllByBookerIdAndStatusAndStartIsAfterOrderByStartDesc(Long bookerId,
                                                                                  BookingStatus status, LocalDateTime dt);

    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId,
                                                                   BookingStatus status);

    Collection<Booking> findAllByItemIdOrderByStartAsc(Long itemId);

    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    Collection<Booking> findAllByItemOwnerIdAndStatusAndEndIsAfterOrderByStartDesc(Long ownerId,
                                                                                   BookingStatus status, LocalDateTime dt);

    Collection<Booking> findAllByItemOwnerIdAndStatusAndEndIsBeforeOrderByStartDesc(Long ownerId,
                                                                                    BookingStatus status, LocalDateTime dt);

    Collection<Booking> findAllByItemOwnerIdAndStatusAndStartIsAfterOrderByStartDesc(Long ownerId,
                                                                                     BookingStatus status, LocalDateTime dt);

    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId,
                                                                      BookingStatus status);
}
