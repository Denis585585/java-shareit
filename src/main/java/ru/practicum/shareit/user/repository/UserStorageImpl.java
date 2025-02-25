package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.EmailValidException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class UserStorageImpl implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Long id = 1L;

    @Override
    public User createUser(User user) {
        if (emails.contains(user.getEmail())) {
            throw new EmailValidException("A user with this email= " + user.getEmail() + " already exists");
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
            throw new NotFoundException("User with this id= " + userId + " not found");
        }
        return users.get(userId);
    }

    @Override
    public User updateUser(Long userId, User user) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User with this id= " + userId + " not found");
        }

        User updatedUser = users.get(userId);
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (emails.contains(user.getEmail())) {
            throw new EmailValidException("A user with this email= " + user.getEmail() + " already exists");
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
            throw new NotFoundException("User with this id= " + userId + " not found");
        }
        emails.remove(users.get(userId).getEmail());
        users.remove(userId);
    }
}
