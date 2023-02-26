package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private UserDto userDto1;
    private UserDto userDto2;
    private ItemDto itemDto;
    private ItemBooking itemBooking;
    private CommentDto commentDto;
    private static final String userHeader = "X-Sharer-User-Id";

    @BeforeEach
    void start() {
        User user1 = new User(1L, "name1", "email1@mail.com");
        User user2 = new User(2L, "name2", "email2@mail.com");
        userDto1 = UserMapper.toUserDto(user1);
        userDto2 = UserMapper.toUserDto(user2);
        Item item = new Item(1L, "item", "description", true, user1, null);
        itemBooking = ItemMapper.toItemBooking(item);
        itemDto = ItemMapper.toItemDto(item);
        Comment comment = new Comment(1L, "text", item, user2);
        commentDto = CommentMapper.toCommentDto(comment);
    }

    @Test
    void findAll() throws Exception {
        when(itemService.findAll(anyLong()))
                .thenReturn(List.of(itemBooking));
        mockMvc.perform(get("/items")
                        .header(userHeader, userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemBooking))));
    }

    @Test
    void findItemById() throws Exception {
        when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(itemBooking);
        mockMvc.perform(get("/items/1")
                        .header(userHeader, userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemBooking)));
    }

    @Test
    void create() throws Exception {
        when(itemService.create(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

    @Test
    void put() throws Exception {
        when(itemService.put(anyLong(), any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);
        mockMvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

    @Test
    void search() throws Exception {
        when(itemService.search(anyString()))
                .thenReturn(List.of(itemDto));
        mockMvc.perform(get("/items/search")
                        .param("text", "Item1")
                        .header(userHeader, userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);
        mockMvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(userHeader, userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }
}
