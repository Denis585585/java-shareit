package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentNewDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    @Mapping(source = "author.name", target = "authorName")
    CommentDto toCommentDto(Comment comment);

    List<CommentDto> toCommentDto(Iterable<Comment> comments);

    @Mapping(source = "commentDto.id", target = "id")
    @Mapping(source = "commentDto.text", target = "text")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "author", target = "author")
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    Comment toComment(CommentNewDto commentDto, Item item, User author);
}
