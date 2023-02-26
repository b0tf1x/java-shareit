package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto create(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, user));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    private List<ItemRequestDto> addItemsToRequests(List<ItemRequestDto> requestsDtoList) {
        List<Long> requestsIds = new ArrayList<>();
        requestsDtoList.forEach(itemRequestDto -> requestsIds.add(itemRequestDto.getId()));
        List<ItemDto> items = itemRepository.findByRequestsIds(requestsIds).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        Map<Long, ItemRequestDto> requestsMap = requestsDtoList.stream().collect(Collectors.toMap(ItemRequestDto::getId, film -> film, (a, b) -> b));
        items.forEach(itemDto -> requestsMap.get(itemDto.getRequestId()).getItems().add(itemDto));
        return new ArrayList<>(requestsMap.values());
    }

    @Override
    public List<ItemRequestDto> getRequestsInformation(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        List<ItemRequestDto> requestsList = itemRequestRepository.findAllByRequestorId(userId).stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        return addItemsToRequests(requestsList);
    }

    @Override
    public ItemRequestDto getRequestInformation(long userId, long request) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        ItemRequest itemRequest = itemRequestRepository.findById(request).orElseThrow(() -> {
            throw new NotFoundException("Запрос не найден");
        });
        List<ItemDto> itemsList = itemRepository.findByItemRequestId(request).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemsList);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId, int from, int size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<ItemRequestDto> requestDtoList = itemRequestRepository.findAllPages(userId, pageRequest).stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
        return addItemsToRequests(requestDtoList);
    }
}
