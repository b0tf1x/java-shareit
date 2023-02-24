package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
        {
            throw new NotFoundException("Пользователь не найден");
        });
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, user));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }
    private List<ItemRequestDto> addItemsToRequests(List<ItemRequestDto> requestsDtoList){
        List<Long>requestsIds = new ArrayList<>();
        requestsDtoList.forEach(itemRequestDto -> requestsIds.add(itemRequestDto.getId()));
        List<ItemDto> items = itemRepository.findByRequestsIds()
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        items.forEach(itemDto -> requestsDtoList.get(itemDto.getRequestId()).getItems().add(itemDto));
    }
    @Override
    public List<ItemRequestDto> getRequestsInformation(long userId){
        User user = userRepository.findById(userId).orElseThrow(() ->
        {
            throw new NotFoundException("Пользователь не найден");
        });
     List<ItemRequestDto> requestsList = itemRequestRepository.findAllByRequestorId(userId)
             .stream()
             .map(ItemRequestMapper::toItemRequestDto)
             .collect(Collectors.toList());

    }
}
