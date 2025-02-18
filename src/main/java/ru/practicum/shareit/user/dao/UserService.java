package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto getUser(Long userId);

    UserDto addUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);
}
