package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.FailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    private LocalDateTime start;
    private LocalDateTime end;
    private User user1;
    private User user2;
    private Item item;
    private Booking booking;

    @BeforeEach
    void start() {
        start = LocalDateTime.now().plusHours(1);
        end = LocalDateTime.now().plusHours(2);
        user1 = new User(1L, "name1", "email1@mail.com");
        user2 = new User(2L, "name2", "email2@mail.com");
        item = new Item(1L, "name", "description", true, user1, null);
        booking = new Booking(1L, start, end, user2, item, Status.WAITING);
    }

    @Test
    void create() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        Booking newBooking = bookingService.create(user2.getId(), BookingMapper.toBookingDto(booking));
        assertEquals(1, newBooking.getId());
        assertEquals(start, newBooking.getStart());
        assertEquals(end, newBooking.getEnd());
        assertEquals(item, newBooking.getItem());
        assertEquals(user2, newBooking.getBooker());
    }

    @Test
    void createWithWrongBooker() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        assertThrows(NotFoundException.class, () ->
                bookingService.create(user1.getId(), BookingMapper.toBookingDto(booking)));
    }

    @Test
    void createWithWrongItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        item.setAvailable(false);
        assertThrows(FailException.class, () ->
                bookingService.create(user2.getId(), BookingMapper.toBookingDto(booking)));
    }

    @Test
    void createWrongTime() {
        booking.setStart(LocalDateTime.now().minusHours(2));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        assertThrows(NotFoundException.class, () ->
                bookingService.create(user1.getId(), BookingMapper.toBookingDto(booking)));
    }

    @Test
    void updateStatusWrongUser() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        assertThrows(NotFoundException.class, () ->
                bookingService.updateStatus(user2.getId(), booking.getId(), true));
    }

    @Test
    void updateStatusWrongId() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
                bookingService.updateStatus(user2.getId(), booking.getId(), true));
    }

    @Test
    void updateStatusRejected() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        Booking booking1 = bookingService.updateStatus(user1.getId(), booking.getId(), false);
        assertEquals(Status.REJECTED, booking1.getStatus());
    }

    @Test
    void getByBooker() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByBookerOrderByStartDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        List<Booking> bookingList = bookingService.getByBooker(user1.getId(), "ALL", 0, 10);
        assertEquals(1, bookingList.size());
        assertEquals(1, bookingList.get(0).getId());
        assertEquals(start, bookingList.get(0).getStart());
        assertEquals(end, bookingList.get(0).getEnd());
        assertEquals(item, bookingList.get(0).getItem());
        assertEquals(user2, bookingList.get(0).getBooker());
        assertEquals(Status.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void getByBookerCurrent() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerCurrent(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        List<Booking> bookingList = bookingService.getByBooker(user1.getId(), "CURRENT", 0, 10);
        assertEquals(1, bookingList.size());
        assertEquals(1, bookingList.get(0).getId());
        assertEquals(start, bookingList.get(0).getStart());
        assertEquals(end, bookingList.get(0).getEnd());
        assertEquals(item, bookingList.get(0).getItem());
        assertEquals(user2, bookingList.get(0).getBooker());
        assertEquals(Status.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void getByBookerPast() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerPast(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        List<Booking> bookingList = bookingService.getByBooker(user1.getId(), "PAST", 0, 10);
        assertEquals(1, bookingList.size());
        assertEquals(1, bookingList.get(0).getId());
        assertEquals(start, bookingList.get(0).getStart());
        assertEquals(end, bookingList.get(0).getEnd());
        assertEquals(item, bookingList.get(0).getItem());
        assertEquals(user2, bookingList.get(0).getBooker());
        assertEquals(Status.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void getByBookerFuture() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerFuture(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        List<Booking> bookingList = bookingService.getByBooker(user1.getId(), "FUTURE", 0, 10);
        assertEquals(1, bookingList.size());
        assertEquals(1, bookingList.get(0).getId());
        assertEquals(start, bookingList.get(0).getStart());
        assertEquals(end, bookingList.get(0).getEnd());
        assertEquals(item, bookingList.get(0).getItem());
        assertEquals(user2, bookingList.get(0).getBooker());
        assertEquals(Status.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void getByBookerWaiting() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerAndState(anyLong(), any(Status.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        List<Booking> bookingList = bookingService.getByBooker(user1.getId(), "WAITING", 0, 10);
        assertEquals(1, bookingList.size());
        assertEquals(1, bookingList.get(0).getId());
        assertEquals(start, bookingList.get(0).getStart());
        assertEquals(end, bookingList.get(0).getEnd());
        assertEquals(item, bookingList.get(0).getItem());
        assertEquals(user2, bookingList.get(0).getBooker());
        assertEquals(Status.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void getByBookerRejected() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerAndState(anyLong(), any(Status.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        List<Booking> bookingList = bookingService.getByBooker(user1.getId(), "REJECTED", 0, 10);
        assertEquals(1, bookingList.size());
        assertEquals(1, bookingList.get(0).getId());
        assertEquals(start, bookingList.get(0).getStart());
        assertEquals(end, bookingList.get(0).getEnd());
        assertEquals(item, bookingList.get(0).getItem());
        assertEquals(user2, bookingList.get(0).getBooker());
        assertEquals(Status.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void getByBookerWrongUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.getByBooker(user1.getId(), "WAITING", 0, 10));
    }

    @Test
    void getByBookerWrongState() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        assertThrows(UnsupportedStateException.class, () -> bookingService.getByBooker(user1.getId(), "wrong", 0, 10));
    }

    @Test
    void getBookingInformationWrongBooking() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
                bookingService.getBookingInformation(user1.getId(), booking.getId()));
    }

    @Test
    void getBookingInformationNotOwner() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        booking.setBooker(user2);
        assertThrows(NotFoundException.class, () ->
                bookingService.getBookingInformation(5L, booking.getId()));
    }

    @Test
    void getByOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.getByOwner(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        List<Booking> bookingList = bookingService.getByOwner(user1.getId(), "ALL", 0, 10);
        assertEquals(1, bookingList.size());
        assertEquals(1, bookingList.get(0).getId());
        assertEquals(start, bookingList.get(0).getStart());
        assertEquals(end, bookingList.get(0).getEnd());
        assertEquals(item, bookingList.get(0).getItem());
        assertEquals(user2, bookingList.get(0).getBooker());
        assertEquals(Status.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void getByOwnerCurrent() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.getByOwnerCurrent(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        List<Booking> bookingList = bookingService.getByOwner(user1.getId(), "CURRENT", 0, 10);
        assertEquals(1, bookingList.size());
        assertEquals(1, bookingList.get(0).getId());
        assertEquals(start, bookingList.get(0).getStart());
        assertEquals(end, bookingList.get(0).getEnd());
        assertEquals(item, bookingList.get(0).getItem());
        assertEquals(user2, bookingList.get(0).getBooker());
        assertEquals(Status.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void getByOwnerPast() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.getByOwnerPast(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        List<Booking> bookingList = bookingService.getByOwner(user1.getId(), "PAST", 0, 10);
        assertEquals(1, bookingList.size());
        assertEquals(1, bookingList.get(0).getId());
        assertEquals(start, bookingList.get(0).getStart());
        assertEquals(end, bookingList.get(0).getEnd());
        assertEquals(item, bookingList.get(0).getItem());
        assertEquals(user2, bookingList.get(0).getBooker());
        assertEquals(Status.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void getByOwnerFuture() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.getByOwnerFuture(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        List<Booking> bookingList = bookingService.getByOwner(user1.getId(), "FUTURE", 0, 10);
        assertEquals(1, bookingList.size());
        assertEquals(1, bookingList.get(0).getId());
        assertEquals(start, bookingList.get(0).getStart());
        assertEquals(end, bookingList.get(0).getEnd());
        assertEquals(item, bookingList.get(0).getItem());
        assertEquals(user2, bookingList.get(0).getBooker());
        assertEquals(Status.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void getByOwnerWaiting() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByOwnerAndState(anyLong(), any(Status.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        List<Booking> bookingList = bookingService.getByOwner(user1.getId(), "WAITING", 0, 10);
        assertEquals(1, bookingList.size());
        assertEquals(1, bookingList.get(0).getId());
        assertEquals(start, bookingList.get(0).getStart());
        assertEquals(end, bookingList.get(0).getEnd());
        assertEquals(item, bookingList.get(0).getItem());
        assertEquals(user2, bookingList.get(0).getBooker());
        assertEquals(Status.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void getByOwnerRejected() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByOwnerAndState(anyLong(), any(Status.class), any(PageRequest.class)))
                .thenReturn(List.of(booking));
        List<Booking> bookingList = bookingService.getByOwner(user1.getId(), "REJECTED", 0, 10);
        assertEquals(1, bookingList.size());
        assertEquals(1, bookingList.get(0).getId());
        assertEquals(start, bookingList.get(0).getStart());
        assertEquals(end, bookingList.get(0).getEnd());
        assertEquals(item, bookingList.get(0).getItem());
        assertEquals(user2, bookingList.get(0).getBooker());
        assertEquals(Status.WAITING, bookingList.get(0).getStatus());
    }

    @Test
    void geyByOwnerWrongState() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        assertThrows(UnsupportedStateException.class, () -> bookingService.getByOwner(user1.getId(), "wrong", 0, 10));
    }

    @Test
    void getByOwnerWrongUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.getByOwner(user1.getId(), "WAITING", 0, 10));

    }
}
