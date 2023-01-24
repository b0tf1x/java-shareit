package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long currentId = 1;

    @Override
    public List<UserDto> findAll() {
        List<User> result = new ArrayList<>(users.values());
        List<UserDto> resultDto = new ArrayList<>();
        for (User user : result) {
            resultDto.add(UserMapper.toUserDto(user));
        }
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
        return userDto;
    }

    public UserDto put(long userId, UserDto userDto) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        User user = users.get(userId);
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
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
        users.remove(userId);
    }
}
