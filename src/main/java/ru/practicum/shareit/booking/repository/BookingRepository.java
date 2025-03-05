package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b " +
            "FROM Booking as b" +
            "WHERE b.item.id= ?1 " +
            "AND b.status IN ?2 " +
            "AND ?3 <= b.end.date " +
            "AND ?4 >= b.start.date")
    Collection<Booking> findAllRequaredDates(Long itemId, Set<BookingStatus> status, LocalDateTime startDate,
                                             LocalDateTime endDate);

    Collection<Booking> findAllByItemAndBookerId(Long itemId, Long bookerId);

    Collection<Booking> findAllByBookerOrderByStartDesc(User booker); //ALL

    Collection<Booking> findAllByBookerAndStatusAndEndIsAfterOrderByStartDesc(User booker, BookingStatus status, //CURRENT
                                                                              LocalDateTime date);

    Collection<Booking> findAllByBookerAndStatusAndEndIsBeforeOrderByStartDesc(User booker, BookingStatus status, //PAST
                                                                               LocalDateTime date);

    Collection<Booking> findAllByBookerAndStatusAndStartIsAfterOrderByStartDesc(User booker, BookingStatus status, //FUTURE
                                                                                LocalDateTime date);

    Collection<Booking> findAllByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status); // WAITING and REJECTED

    @Query("SELECT b " +
            "FROM Booking as b " +
            "WHERE b.item.id IN ?1 " +
            "AND (b.end.date < ?2 OR b.start.date > ?2)")
    Collection<Booking> findAllByItemId(Collection<Long> itemIds, LocalDateTime dateTime);

    Collection<Booking> findAllByItemOwnerOrderByStartDesc(User owner); //ALL

    Collection<Booking> findAllByItemOwnerAndStatusAndEndIsAfterOrderByStartDesc(User owner, BookingStatus status, //CURRENT
                                                                                 LocalDateTime date);

    Collection<Booking> findAllByItemOwnerAndStatusAndEndIsBeforeOrderByStartDesc(User owner, BookingStatus status, //PAST
                                                                                  LocalDateTime date);

    Collection<Booking> findAllByItemOwnerAndStatusAndStartIsAfterOrderByStartDesc(User owner, BookingStatus status, //FUTURE
                                                                                   LocalDateTime date);

    Collection<Booking> findAllByItemOwnerAndStatusOrderByStartDesc(User owner, BookingStatus status); // WAITING and REJECTED
}
