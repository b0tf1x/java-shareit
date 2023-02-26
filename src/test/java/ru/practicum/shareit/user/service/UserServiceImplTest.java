package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;

    @BeforeEach
    void start() {
        user = new User(1L, "name", "email@mail.com");
    }

    @Test
    void findAll() {
        when(userRepository.findAll())
                .thenReturn(List.of(user));
        List<UserDto> usersDto = userService.findAll();
        assertEquals(1, usersDto.size());
        assertEquals(1, usersDto.get(0).getId());
        assertEquals("name", usersDto.get(0).getName());
        assertEquals("email@mail.com", usersDto.get(0).getEmail());
    }

    @Test
    void findUserById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        UserDto userDto = userService.findUserById(user.getId());
        assertEquals(1, userDto.getId());
        assertEquals("name", user.getName());
        assertEquals("email@mail.com", userDto.getEmail());
    }

    @Test
    void findUserWrongId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
                userService.findUserById(10L));
    }

    @Test
    void create() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto userDto = userService.create(UserMapper.toUserDto(user));
        assertEquals(1, userDto.getId());
        assertEquals("name", userDto.getName());
        assertEquals("email@mail.com", userDto.getEmail());
    }

    @Test
    void putWrongUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        UserDto userDto = UserMapper.toUserDto(user);
        userDto.setId(0L);
        assertThrows(NotFoundException.class, () ->
                userService.put(1L, userDto));
    }

    @Test
    void put() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto userDto = UserMapper.toUserDto(user);
        userDto.setName("updated");
        userDto.setEmail("updated@mail.com");
        UserDto userDto1 = userService.put(userDto.getId(), userDto);
        assertEquals(1, userDto1.getId());
        assertEquals("updated", userDto1.getName());
        assertEquals("updated@mail.com", userDto1.getEmail());
    }
}
