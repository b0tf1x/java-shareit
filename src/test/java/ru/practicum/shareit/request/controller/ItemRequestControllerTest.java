package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;
    private User user;
    private ItemRequestDto itemRequestDto;
    private static final String userHeader = "X-Sharer-User-Id";

    @BeforeEach
    void start() throws Exception {
        user = new User(1L, "name", "email@mail.com");
        itemRequestDto = new ItemRequestDto(1L, 1L, "description", LocalDateTime.now(), new ArrayList<>());
    }

    @Test
    void create() throws Exception {
        when(itemRequestService.create(anyLong(), any()))
                .thenReturn(itemRequestDto);
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestsInformation() throws Exception {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        when(itemRequestService.getRequestsInformation(anyLong()))
                .thenReturn(List.of(itemRequestDto));
        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestInformation() throws Exception {
        when(itemRequestService.getRequestInformation(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);
        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));
        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(userHeader, 1L))
                .andExpect(status().isOk());
    }
}
