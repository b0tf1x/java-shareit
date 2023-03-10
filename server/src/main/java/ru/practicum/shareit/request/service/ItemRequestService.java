package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getRequestsInformation(long userId);

    ItemRequestDto getRequestInformation(long userId, long request);

    List<ItemRequestDto> getAllRequests(long userId, int from, int size);
}