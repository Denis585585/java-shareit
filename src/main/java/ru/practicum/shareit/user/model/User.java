package ru.practicum.shareit.user.model;

import lombok.*;


@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
}
