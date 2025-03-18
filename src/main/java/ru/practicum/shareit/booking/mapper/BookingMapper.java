package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingNewDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.util.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {

    BookingDto toBookingDto(Booking booking);

    @Mapping(source = "bookingDto.id", target = "id")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "booker", target = "booker")
    @Mapping(source = "bookingDto.start", target = "start")
    @Mapping(source = "bookingDto.end", target = "end")
    @Mapping(source = "status", target = "status")
    Booking toBooking(BookingNewDto bookingDto, ItemDto item, UserDto booker, BookingStatus status);
}
