package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> jacksonTester;
    private BookingDto bookingDto;
    private User user1;
    private User user2;
    private Item item;
    private Booking booking;

    @BeforeEach
    void start() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusHours(1);
        LocalDateTime end = now.plusHours(2);
        user1 = new User(1L, "name1", "email1@mail.com");
        user2 = new User(2L, "name2", "email@mail.com");
        item = new Item(1L, "name", "description", true, user1, null);
        booking = new Booking(1L, start, end, user2, item, Status.WAITING);
        bookingDto = BookingMapper.toBookingDto(booking);
    }

    @Test
    void testJson() throws Exception {
        JsonContent<BookingDto> json = jacksonTester.write(bookingDto);
        Integer id = Math.toIntExact(bookingDto.getId());
        Integer item = Math.toIntExact(bookingDto.getItemId());
        Integer booker = Math.toIntExact(bookingDto.getBookerId());
        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(id);
        assertThat(json).extractingJsonPathNumberValue("$.itemId").isEqualTo(item);
        assertThat(json).extractingJsonPathNumberValue("$.bookerId").isEqualTo(booker);
        assertThat(json).extractingJsonPathStringValue("$.status").isEqualTo(bookingDto.getStatus().toString());
    }
}
