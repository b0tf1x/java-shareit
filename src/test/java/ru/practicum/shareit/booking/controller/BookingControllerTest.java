package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;
    private UserDto userDto1;
    private UserDto userDto2;
    private User user1;
    private User user2;
    private BookingDto bookingDto;
    private Booking booking;
    private static final String userHeader = "X-Sharer-User-Id";
    private Item item;

    @BeforeEach
    void start() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusHours(1);
        LocalDateTime end = now.plusHours(2);
        user1 = new User(1L, "name1", "email1@mail.com");
        user2 = new User(2L, "name2", "email@mail.com");
        userDto1 = UserMapper.toUserDto(user1);
        userDto2 = UserMapper.toUserDto(user2);
        item = new Item(1L, "name", "description", true, user1, null);
        booking = new Booking(1L, start, end, user2, item, Status.WAITING);
        bookingDto = BookingMapper.toBookingDto(booking);
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(anyLong(), any(BookingDto.class)))
                .thenReturn(booking);
        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, userDto2.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));
    }

    @Test
    void updateStatus() throws Exception {
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);
        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/1")
                        .param("approved", "true")
                        .header(userHeader, userDto2.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));
    }

    @Test
    void getBookingInformation() throws Exception {
        when(bookingService.getBookingInformation(anyLong(), anyLong()))
                .thenReturn(booking);
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header(userHeader, userDto2.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(booking)));
    }

    @Test
    void getByBooker() throws Exception {
        when(bookingService.getByBooker(anyLong(), any(String.class), anyInt(), anyInt()))
                .thenReturn(List.of(booking));
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header(userHeader, userDto2.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(booking))));
    }

    @Test
    void getByOwner() throws Exception {
        when(bookingService.getByOwner(anyLong(), any(String.class), anyInt(), anyInt()))
                .thenReturn(List.of(booking));
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header(userHeader, userDto2.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(booking))));
    }
}

