package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class RequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;
    private ItemRequest itemRequest;
    @BeforeEach
    void start(){
        LocalDateTime now = LocalDateTime.now();
        user = new User(1L,"name","email@mail.com");
        user = userRepository.save(user);
        itemRequest = new ItemRequest(1L,"description",user,now);
        itemRequest = itemRequestRepository.save(itemRequest);
    }
    @AfterEach
    void delete(){
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
    @Test
    void findByRequestsIds(){
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorId(user.getId());
        assertEquals(List.of(itemRequest),itemRequestList);
    }
}
