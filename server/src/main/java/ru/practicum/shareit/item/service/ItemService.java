package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemBooking;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemBooking> findAll(long userId);

    ItemBooking findItemById(long userId, long itemId);

    ItemDto create(long userId, ItemDto itemDto);

    ItemDto put(long userId, ItemDto itemDto, long itemId);

    List<ItemDto> search(String text);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}

