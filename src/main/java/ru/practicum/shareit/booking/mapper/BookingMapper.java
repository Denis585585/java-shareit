package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {

    BookingDto toBookingDto(Booking booking);

    @Mapping(source = "bookingDto.id", target = "id")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "user", target = "booker")
    Booking toBooking(BookingDto bookingDto, Item item, User user, BookingStatus status);
}
