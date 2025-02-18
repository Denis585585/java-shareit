package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailValidException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Long id = 1L;

    @Override
    public User createUser(User user) {
        if (emails.contains(user.getEmail())) {
            throw new EmailValidException("Пользователь с таким email уже существует");
        }
        emails.add(user.getEmail());
        user.setId(id);
        users.put(user.getId(), user);
        id++;
        return user;
    }

    @Override
    public User getUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User with id  not found");
        }
        return users.get(userId);
    }

    @Override
    public User updateUser(Long userId, User user) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User with id not found");
        }
        User updatedUser = users.get(userId);
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (emails.contains(user.getEmail())) {
            throw new EmailValidException("Пользователь с  email уже существует");
        }
        if (user.getEmail() != null) {
            emails.remove(users.get(userId).getEmail());
            emails.add(user.getEmail());
            updatedUser.setEmail(user.getEmail());
        }
        users.put(userId, updatedUser);
        return updatedUser;
    }

    @Override
    public void deleteUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        emails.remove(users.get(userId).getEmail());
        users.remove(userId);
    }
}
