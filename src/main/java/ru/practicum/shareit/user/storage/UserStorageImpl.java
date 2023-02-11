package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long currentId = 1;

    @Override
    public Set<String> getEmails() {
        return emails;
    }

    @Override
    public List<UserDto> findAll() {
        List<UserDto> resultDto = new ArrayList<>();
        users.values().forEach(user -> resultDto.add(UserMapper.toUserDto(user)));
        return resultDto;
    }

    @Override
    public UserDto findUserById(long userId) {
        if (users.get(userId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return UserMapper.toUserDto(users.get(userId));
    }

    public UserDto create(UserDto userDto) {
        userDto.setId(currentId++);
        users.put(userDto.getId(), UserMapper.toUser(userDto));
        emails.add(userDto.getEmail());
        return userDto;
    }

    public UserDto put(long userId, UserDto userDto) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        User user = users.get(userId);
        if (userDto.getEmail() != null) {
            emails.remove(user.getEmail());
            user.setEmail(userDto.getEmail());
            emails.add(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        users.remove(userId);
        users.put(userId, user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(long userId) {
        emails.remove(users.get(userId).getEmail());
        users.remove(userId);
    }
}
