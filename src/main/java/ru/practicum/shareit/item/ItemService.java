package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> findAll(long userId);

    ItemDto findItemById(long itemId);

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto put(long userId, ItemDto itemDto, long itemId);

    List<ItemDto> search(String text);
}
