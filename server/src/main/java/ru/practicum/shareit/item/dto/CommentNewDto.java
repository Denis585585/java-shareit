package ru.practicum.shareit.item.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentNewDto {
    private Long id;
    private String text;
}
