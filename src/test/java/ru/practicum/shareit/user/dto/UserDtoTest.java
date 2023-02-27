package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> jacksonTester;
    private UserDto userDto;
    private User user;

    @BeforeEach
    void start() {
        user = new User(1L, "name1", "email1@mail.com");
        userDto = UserMapper.toUserDto(user);
    }

    @Test
    void testJson() throws Exception {
        JsonContent<UserDto> json = jacksonTester.write(userDto);
        assertThat(json).extractingJsonPathNumberValue(
                "$.id").isEqualTo(userDto.getId());
        assertThat(json).extractingJsonPathStringValue(
                "$.name").isEqualTo(userDto.getName());
        assertThat(json).extractingJsonPathStringValue(
                "$.email").isEqualTo(userDto.getEmail());
    }
}
