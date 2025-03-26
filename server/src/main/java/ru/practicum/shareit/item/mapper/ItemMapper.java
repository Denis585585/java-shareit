package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    @Mapping(source = "itemDto.id", target = "id")
    @Mapping(source = "itemDto.name", target = "name")
    @Mapping(source = "user", target = "owner")
    Item toItem(ItemDto itemDto, User user);

    static List<ItemResponseDto> toItemResponseDto(List<Item> items) {
        if (items == null) {
            return List.of();
        }
        List<ItemResponseDto> itemResponseDtos = new ArrayList<>();
        for (Item item : items) {
            if (item != null) {
                itemResponseDtos.add(new ItemResponseDto(item.getId(), item.getName(), item.getOwner().getId()));
            }
        }
        return itemResponseDtos;
    }
}
