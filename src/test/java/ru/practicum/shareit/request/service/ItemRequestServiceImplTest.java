package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.storage.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    private User user;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private Item item;

    @BeforeEach
    void start() {
        user = new User(1L, "name", "email@mail.com");
        itemRequestDto = new ItemRequestDto(1L, 1L, "description", LocalDateTime.now(), new ArrayList<>());
        itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        item = new Item(1L, "name", "description", true, user, null);
    }

    @Test
    void create() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequestDto newItemRequest = itemRequestService.create(user.getId(), itemRequestDto);
        itemRequestDto.setCreated(newItemRequest.getCreated());
        assertEquals(itemRequestDto.getId(), newItemRequest.getId());
    }

    @Test
    void createWrongUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.create(1L, itemRequestDto));
    }

    @Test
    void getRequestsInformationEmpty() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        List<ItemRequestDto> requestDtoList = itemRequestService.getRequestsInformation(user.getId());
        assertEquals(0, requestDtoList.size());
    }

    @Test
    void getRequestsInformationWrongUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestsInformation(1L));
    }

    @Test
    void getRequestInformationWrongRequest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestInformation(user.getId(), 1L));
    }

    @Test
    void getRequestInformationWrongUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestInformation(user.getId(), 1L));
    }

    @Test
    void getRequestInformation() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        item.setItemRequest(itemRequest);
        when(itemRepository.findByItemRequestId(anyLong()))
                .thenReturn(List.of(item));
        ItemRequestDto newItemRequestDto = itemRequestService.getRequestInformation(user.getId(), itemRequestDto.getId());
        assertNotNull(newItemRequestDto);
    }

    @Test
    void getAllRequests() {
        when(itemRequestRepository.findAllPages(anyLong(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getAllRequests(user.getId(), 0, 10);
        assertEquals(1, itemRequestDtoList.size());
        assertEquals(1, itemRequestDtoList.get(0).getId());
        assertEquals("description", itemRequestDtoList.get(0).getDescription());
        assertEquals(user.getId(), itemRequestDtoList.get(0).getRequestorId());
        assertEquals(0, itemRequestDtoList.get(0).getItems().size());
    }
}
