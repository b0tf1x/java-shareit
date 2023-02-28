package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void start() {
        user = new User(1L, "name", "email@mail.com");
        userRepository.save(user);
    }

    @Test
    void findByIdTest() {
        assertNotNull(userRepository.findById(user.getId()));
    }

    @Test
    void findAllTest() {
        List<User> userList = userRepository.findAll();
        assertEquals(1, userList.size());
        assertEquals(user, userList.get(0));
    }
}
