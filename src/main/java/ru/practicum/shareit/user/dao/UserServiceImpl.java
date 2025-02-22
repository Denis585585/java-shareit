package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserStorage;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.userToUserDto(userStorage.getUser(userId));
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        return UserMapper.userToUserDto(userStorage.addUser(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        return UserMapper.userToUserDto(userStorage.updateUser(userId, UserMapper.toUser(userDto)));
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }
}
