package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<UserDto> findAll() {
        return userStorage.findAll();
    }

    @Override
    public UserDto findUserById(long userId) {
        return userStorage.findUserById(userId);
    }

    @Override
    public UserDto create(UserDto userDto) {

        validation(userDto.getEmail());
        return userStorage.create(userDto);
    }

    @Override
    public UserDto put(long userId, UserDto userDto) {
        if (userDto.getEmail() != null) {
            validation(userDto.getEmail());
        }
        return userStorage.put(userId, userDto);
    }

    @Override
    public void delete(long userId) {
        userStorage.delete(userId);
    }

    private void validation(String email) {
        List<UserDto> usersDto = userStorage.findAll();
        for (UserDto userDto : usersDto) {
            if (userDto.getEmail().equals(email)) {
                throw new ValidationException("Одинаковый email");
            }
        }
    }
}
