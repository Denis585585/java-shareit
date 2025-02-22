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
    private final UserMapper userMapper;

    @Override
    public UserDto getUser(Long userId) {
        return userMapper.userToUserDto(userStorage.getUser(userId));
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        return userMapper.userToUserDto(userStorage.createUser(userMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        return userMapper.userToUserDto(userStorage.updateUser(userId, userMapper.toUser(userDto)));
    }

    @Override
    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }
}
