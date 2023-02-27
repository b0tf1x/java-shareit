package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> jacksonTester;
    private ItemDto itemDto;
    private User user;
    private Item item;

    @BeforeEach
    void start() {
        user = new User(1L, "name1", "email1@mail.com");
        item = new Item(1L, "name", "description", true, user, null);
        itemDto = ItemMapper.toItemDto(item);
    }

    @Test
    void testJson() throws Exception {
        JsonContent<ItemDto> json = jacksonTester.write(itemDto);
        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(itemDto.getId()));
        assertThat(json).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDto.getName());
        assertThat(json).extractingJsonPathStringValue(
                "$.description").isEqualTo(itemDto.getDescription());
        assertThat(json).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDto.getAvailable());
    }
}
