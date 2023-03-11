package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exeption.NotFoundException;
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
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    private User user1;

    @BeforeEach
    void beforeEach() {
        user1 = new User(1L, "User1 name", "user1@mail.com");
    }

    @Test
    void createUserTest() {
        when(repository.save(any(User.class))).thenReturn(user1);
        UserDto userDto = service.create(
                UserMapper.toUserDto(user1));
        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }


    @Test
    void updateUserWithEmailFormatTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(repository.save(any(User.class)))
                .thenReturn(user1);
        UserDto userDto = UserMapper.toUserDto(user1);
        service.put(userDto.getId(), userDto);
        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void updateUserWithNoUser() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());
        UserDto userDto = UserMapper.toUserDto(user1);
        userDto.setId(10L);
        assertThrows(NotFoundException.class,
                () -> service.put(1L, userDto)
        );
    }

    @Test
    void getAllUsersWhenUserFoundThenReturnedUser() {

        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        when(repository.save(any(User.class)))
                .thenReturn(user1);

        UserDto userDto = UserMapper.toUserDto(user1);
        service.put(userDto.getId(), userDto);

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void getAllUsersWhenUserFoundThenUserNotFoundExceptionThrown() {
        long userId = 0L;
        when(repository.findById(userId))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getById(userId));
    }

    @Test
    void getByIdTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        UserDto userDto = service.getById(user1.getId());

        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void getUserWrongIdTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.getById(user1.getId()));
    }

    @Test
    void deleteUserTest() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        UserDto userDto = service.delete(user1.getId());
        assertEquals(1, userDto.getId());
        assertEquals("User1 name", userDto.getName());
        assertEquals("user1@mail.com", userDto.getEmail());
    }

    @Test
    void deleteUserTestWithNoUser() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.empty());
        UserDto userDto = UserMapper.toUserDto(user1);
        userDto.setId(10L);
        assertThrows(NotFoundException.class,
                () -> service.delete(1L)
        );
    }

    @Test
    void getAllUsersTest() {
        when(repository.findAll())
                .thenReturn(List.of(user1));
        List<UserDto> userDto = service.getAllUsers();
        assertEquals(1, userDto.size());
        assertEquals(1, userDto.get(0).getId());
        assertEquals("User1 name", userDto.get(0).getName());
        assertEquals("user1@mail.com", userDto.get(0).getEmail());
    }
}