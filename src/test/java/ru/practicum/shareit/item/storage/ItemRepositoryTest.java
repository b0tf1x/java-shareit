package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User user1;
    private User user2;
    private Item item;
    private ItemRequest itemRequest;

    @BeforeEach
    void start() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
        LocalDateTime now = LocalDateTime.now();
        user1 = new User(1L, "name1", "email1@mail.com");
        user1 = userRepository.save(user1);
        user2 = new User(2L, "name1", "email2@mail.com");
        user2 = userRepository.save(user2);
        itemRequest = new ItemRequest(1L, "description", user1, now);
        itemRequest = itemRequestRepository.save(itemRequest);
        item = new Item(1L, "name", "description", true, user1, itemRequest);
        itemRepository.save(item);
    }

    @AfterEach
    void delete() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void findAllByOwnerIdOrderByIdAsc() {
        List<Item> items1 = itemRepository.findAllByOwnerIdOrderByIdAsc(user1.getId());
        List<Item> items2 = new ArrayList<>();
        items2.add(item);
        assertEquals(items2.get(0).getId(), items1.get(0).getId());
        assertEquals(items2.get(0).getOwner(), items1.get(0).getOwner());
        assertEquals(items2.get(0).getDescription(), items1.get(0).getDescription());
    }

    @Test
    void search() {
        String text = "name";
        List<Item> items = itemRepository.search(text);
        assertEquals(1, items.size());
        assertEquals(1, items.get(0).getId());
        assertEquals("name", items.get(0).getName());
    }

}
