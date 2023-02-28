package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime start = now.plusHours(1);
    LocalDateTime end = now.plusHours(2);
    User user2;
    Booking booking;

    @BeforeEach
    void start() {
        User user1 = new User(1L, "name1", "email1@mail.com");
        userRepository.save(user1);
        user2 = new User(2L, "name2", "email2@mail.com");
        userRepository.save(user2);
        Item item = new Item(1L, "name", "description", true, user1, null);
        itemRepository.save(item);
        booking = new Booking(1L, start, end, user2, item, Status.WAITING);
        bookingRepository.save(booking);
    }

    @AfterEach
    void delete() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findAllByBookerOrderByStartDescTest() {
        List<Booking> bookingList = bookingRepository.findAllByBookerOrderByStartDesc(user2.getId(), PageRequest.of(0, 10));
        assertEquals(List.of(booking), bookingList);
    }

}
