package ru.practicum.shareit.user.model;

import lombok.*;


@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
}
