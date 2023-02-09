package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {
    private final Set<String> emails = new HashSet<>();
    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        }));
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        emails.add(userDto.getEmail());
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto put(long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь для обновления не найден");
        });
        if (userDto.getEmail() != null) {
            validation(userDto.getEmail());
            emails.remove(user.getEmail());
            user.setEmail(userDto.getEmail());
            emails.add(user.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        userRepository.save(user);
        return userDto;
    }

    @Override
    public void delete(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь для удаления не найден");
        });
        if (user.getEmail() != null) {
            emails.remove(user.getEmail());
        }
        userRepository.findById(userId).ifPresent(userRepository::delete);
    }

    private void validation(String email) {
        if (emails.contains(email)) {
            throw new ValidationException("Одинаковый email");
        }
    }
}
