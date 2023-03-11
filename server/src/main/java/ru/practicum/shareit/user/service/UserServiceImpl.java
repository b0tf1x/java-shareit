package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = repository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto put(long id, UserDto userDto) {
        User user = repository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        return UserMapper.toUserDto(repository.save(user));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long id) {
        User user = repository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto delete(long id) {
        User user = repository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        repository.findById(id).ifPresent(repository::delete);
        return UserMapper.toUserDto(user);
    }
}
