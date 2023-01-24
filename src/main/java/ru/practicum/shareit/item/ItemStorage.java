package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {
    List<ItemDto> findAll(long userId);

    ItemDto findItemById(long itemId);

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto put(long userId, ItemDto itemDto, long itemId);

    List<ItemDto> search(String text);
}

