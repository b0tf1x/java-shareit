package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto findUserById(long userId);

    UserDto create(UserDto userDto);

    UserDto put(long userId, UserDto userDto);

    void delete(long userId);
}
