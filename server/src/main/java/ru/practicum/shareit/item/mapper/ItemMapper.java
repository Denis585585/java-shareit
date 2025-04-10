package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    @Mapping(source = "itemDto.id", target = "id")
    @Mapping(source = "itemDto.name", target = "name")
    @Mapping(source = "user", target = "owner")
    @Mapping(source = "itemDto.description", target = "description")
    @Mapping(source = "request", target = "request")
    Item toItem(ItemDto itemDto, User user, ItemRequest request);

    @Mapping(source = "item.owner.id", target = "ownerId")
    ItemResponseDto toItemResponseDto(Item item);

    List<ItemResponseDto> toItemResponseDto(Iterable<Item> items);
}
