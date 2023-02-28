package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTester;
    private ItemRequestDto itemRequestDto;
    private User user;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void start() {
        LocalDateTime now = LocalDateTime.now();
        user = new User(1L, "name1", "email1@mail.com");
        ItemRequest itemRequest = new ItemRequest(1L, "description", user, now);
        item = new Item(1L, "name", "description", true, user, itemRequest);
        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemDto = ItemMapper.toItemDto(item);
        itemRequestDto.setItems(List.of(itemDto));
    }

    @Test
    void testJson() throws Exception {
        JsonContent<ItemRequestDto> json = jacksonTester.write(itemRequestDto);
        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(itemRequestDto.getId()));
        assertThat(json).extractingJsonPathNumberValue("$.requestorId").isEqualTo(Math.toIntExact(itemRequestDto.getRequestorId()));
        assertThat(json).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDto.getDescription());
        assertThat(json).extractingJsonPathArrayValue("$.items").isNotEmpty();
    }
}
