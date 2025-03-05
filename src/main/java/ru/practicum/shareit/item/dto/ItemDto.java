package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private User owner;
    @NotNull
    private Boolean available;
    private Long requestId;
    @Future
    @NotNull
    private LocalDateTime nextDateBooking;
    @Past
    @NotNull
    private LocalDateTime pastDateBooking;
    private Collection<CommentDto> comments;
}
