package ru.practicum.shareit.item.storage;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User user1;

    private User user2;

    private ItemRequest itemRequest1;

    private Item item1;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.now();
        user1 = userRepository.save(new User(1L, "User1 name", "user1@mail.com"));
        user2 = userRepository.save(new User(2L, "User2 name", "user2@mail.com"));

        itemRequest1 = itemRequestRepository.save(new ItemRequest(1L, "ItemRequest1 description", user1, now));

        item1 = itemRepository.save(new Item(1L, "Item1 name", "Item1 description", true, user1, itemRequest1));

    }

    @Test
    void findAllByOwnerIdOrderByIdAscTest() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(user1.getId(), PageRequest.of(0, 10));
        List<Item> items1 = new ArrayList<>();
        items1.add(item1);
        assertEquals(items1.get(0).getId(), items.get(0).getId());
        assertEquals(items1.get(0).getName(), items.get(0).getName());
        assertEquals(items1.get(0).getDescription(), items.get(0).getDescription());
    }

    @Test
    void searchByTextTestFindDescription() {
        String text = "description";
        List<Item> items = itemRepository.searchByText(text, PageRequest.of(0, 10));
        assertEquals(List.of(item1).size(), items.size());
        assertEquals(item1.getId(), items.get(0).getId());
        assertEquals(item1.getName(), items.get(0).getName());
    }

    @Test
    void searchByTextTestFindName() {
        String text = "name";
        List<Item> items = itemRepository.searchByText(text, PageRequest.of(0, 10));
        assertEquals(List.of(item1).size(), items.size());
        assertEquals(item1.getId(), items.get(0).getId());
        assertEquals(item1.getName(), items.get(0).getName());
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}