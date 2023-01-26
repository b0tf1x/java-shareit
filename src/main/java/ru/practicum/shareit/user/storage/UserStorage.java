package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {
    public List<String> getEmails();
    List<UserDto> findAll();

    UserDto findUserById(long userId);

    UserDto create(UserDto userDto);

    UserDto put(long userId, UserDto userDto);

    void delete(long userId);
}
