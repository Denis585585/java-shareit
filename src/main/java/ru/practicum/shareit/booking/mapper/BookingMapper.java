package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {

    BookingDto toBookingDto(Booking booking);

    @Mapping(source = "bookingNewDto.itemId", target = "item.id")
    @Mapping(source = "booker.id", target = "id")
    Booking toBooking(BookingNewDto bookingNewDto, Item item, User booker, BookingStatus status);
}
