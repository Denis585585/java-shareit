package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

public interface UserStorage {

    User addUser(User user);

    User getUser(Long userId);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);
}
