package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status IN (:status) " +
            "AND :startDate <= b.end " +
            "AND :endDate >= b.start")
    Collection<Booking> findAllWithIntersectionDates(Long itemId, Set<BookingStatus> status,
                                                     LocalDateTime startDate, LocalDateTime endDate);

    Collection<Booking> findAllByItemIdAndBookerId(Long itemId, Long bookerId);

    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId); //ALL

    Collection<Booking> findAllByBookerIdAndStatusAndEndIsAfterOrderByStartDesc(Long bookerId, BookingStatus status, //CURRENT
                                                                                LocalDateTime date);

    Collection<Booking> findAllByBookerIdAndStatusAndEndIsBeforeOrderByStartDesc(Long bookerId, BookingStatus status, //PAST
                                                                                 LocalDateTime date);

    Collection<Booking> findAllByBookerIdAndStatusAndStartIsAfterOrderByStartDesc(Long bookerId, BookingStatus status, //FUTURE
                                                                                LocalDateTime date);

    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status); // WAITING and REJECTED

    Collection<Booking> findAllByItemIdOrderByStartAsc(Long itemId);

    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId); //ALL

    Collection<Booking> findAllByItemOwnerIdAndStatusAndEndIsAfterOrderByStartDesc(Long ownerId, BookingStatus status, //CURRENT
                                                                                 LocalDateTime date);

    Collection<Booking> findAllByItemOwnerIdAndStatusAndEndIsBeforeOrderByStartDesc(Long ownerId, BookingStatus status, //PAST
                                                                                  LocalDateTime date);

    Collection<Booking> findAllByItemOwnerIdAndStatusAndStartIsAfterOrderByStartDesc(Long ownerId, BookingStatus status, //FUTURE
                                                                                   LocalDateTime date);

    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status); // WAITING and REJECTED
}
