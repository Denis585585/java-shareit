package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "start_date")
    private LocalDateTime start;

    @Column(nullable = false, name = "end_date")
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "booker_id")
    private User booker;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    private BookingStatus status;

}
