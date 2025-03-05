package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto getUser(Long userId) {
        return userMapper.toUserDto((userRepository.findById(userId))
                .orElseThrow(() -> new NotFoundException("User with ID= " + userId + " not found")));
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.save(userMapper.toUser(userDto));
        log.info("User with ID= {} has been created", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User updatedUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with ID= " + userId + " not found"));
        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            updatedUser.setEmail(userDto.getEmail());
        }
        log.info("User with ID= {} has been updated", userId);
        return userMapper.toUserDto(userRepository.save(updatedUser));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with ID= " + userId + " not found");
        }
        userRepository.deleteById(userId);
        log.info("User with ID= {} has been deleted", userId);
    }
}
