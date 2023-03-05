package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    private LocalDateTime now = LocalDateTime.now();
    private User user1;
    private User user2;
    private Item item;
    private Booking booking;
    private Comment comment;
    LocalDateTime start = now.plusHours(1);
    LocalDateTime end = now.plusHours(2);

    @BeforeEach
    void start() {
        user1 = new User(1L, "name1", "email1");
        user2 = new User(2L, "name2", "email2");
        userRepository.save(user1);
        userRepository.save(user2);
        item = new Item(1L, "item name", "item description", true, user1, null);
        booking = new Booking(1L, start, end, user2, item, Status.WAITING);
        comment = new Comment(1L, "text", item, user2);
    }

    @Test
    void findAll() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong()))
                .thenReturn(List.of(item));
        List<ItemBooking> itemBookings = itemService.findAll(user1.getId());
        assertEquals(1, itemBookings.size());
        assertEquals(1, itemBookings.get(0).getId());
        assertEquals("item name", itemBookings.get(0).getName());
        assertEquals("item description", itemBookings.get(0).getDescription());
    }

    @Test
    void findItemById() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        ItemBooking itemBooking = itemService.findItemById(item.getId(), user1.getId());
        assertEquals(1, itemBooking.getId());
        assertEquals("item name", itemBooking.getName());
        assertEquals("item description", itemBooking.getDescription());
        assertEquals(true, itemBooking.getAvailable());
    }

    @Test
    void create() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto itemDto = itemService.create(item.getId(), ItemMapper.toItemDto(item));
        assertEquals(1, itemDto.getId());
        assertEquals("item name", itemDto.getName());
        assertEquals("item description", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void createWrongUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
                itemService.create(user1.getId(), ItemMapper.toItemDto(item)));
    }

    @Test
    void createWrongRequest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setRequestId(0L);
        assertThrows(NotFoundException.class, () ->
                itemService.create(user1.getId(), itemDto));
    }


    @Test
    void put() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto itemDto = itemService.put(user1.getId(), ItemMapper.toItemDto(item), item.getId());
        assertEquals(1, itemDto.getId());
        assertEquals("item name", itemDto.getName());
        assertEquals("item description", itemDto.getDescription());
        assertEquals(true, itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void putWrongUser() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        assertThrows(NotFoundException.class, () ->
                itemService.put(user2.getId(), ItemMapper.toItemDto(item), 999L));
    }

    @Test
    void putWrongItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
                itemService.findItemById(2L, item.getId()));
    }

    @Test
    void searchWrong() {
        List<ItemDto> itemsList = itemService.search("Item1");
        assertEquals(Collections.emptyList(), itemsList);
    }

    @Test
    void search() {
        when(itemRepository.search(anyString()))
                .thenReturn(List.of(item));
        List<ItemDto> itemsDto = itemService.search("item");
        assertEquals(1, itemsDto.size());
        assertEquals(1, itemsDto.get(0).getId());
        assertEquals("item name", itemsDto.get(0).getName());
        assertEquals("item description", itemsDto.get(0).getDescription());
        assertEquals(true, itemsDto.get(0).getAvailable());
        assertNull(itemsDto.get(0).getRequestId());
    }

    @Test
    void searchBlank() {
        when(itemRepository.search(anyString()))
                .thenReturn(List.of(item));
        List<ItemDto> itemsDto = itemService.search("");
        assertEquals(Collections.emptyList(), itemsDto);
    }

    @Test
    void addComment() {
        when(bookingRepository.findByBookerIdAndItemIdAndEndBefore(
                anyLong(),
                anyLong(),
                any(LocalDateTime.class)))
                .thenReturn(Optional.ofNullable(booking));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);
        CommentDto commentDto = itemService.addComment(1L, 1L, CommentMapper.toCommentDto(comment));
        assertEquals(1, commentDto.getId());
        assertEquals("text", commentDto.getText());
        assertEquals("name1", commentDto.getAuthorName());
    }

}
