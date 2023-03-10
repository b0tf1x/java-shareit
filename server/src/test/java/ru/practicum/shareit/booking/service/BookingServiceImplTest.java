package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.UnsupportedStateException;
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
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    private LocalDateTime start;

    private LocalDateTime end;

    private User user1;

    private User user2;

    private Item item1;

    private Booking booking1;

    @BeforeEach
    void beforeEach() {

        start = LocalDateTime.now().plusDays(1);
        end = LocalDateTime.now().plusDays(2);

        user1 = new User(1L, "User1 name", "user1@mail.com");
        user2 = new User(2L, "User2 name", "user2@mail.com");

        item1 = Item.builder()
                .id(1L)
                .name("Item1 name")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .itemRequest(null)
                .build();

        booking1 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user2)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void createBookingTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        BookingDtoResponse bookingDtoResponse = bookingService.create(
                user2.getId(),
                BookingMapper.toBookingDto(booking1)
        );

        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(start, bookingDtoResponse.getStart());
        assertEquals(end, bookingDtoResponse.getEnd());
        assertEquals(item1, bookingDtoResponse.getItem());
        assertEquals(user2, bookingDtoResponse.getBooker());
    }

    @Test
    void createBookingWithBookerAsOwnerUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        assertThrows(NotFoundException.class,
                () -> bookingService.create(user1.getId(),
                        BookingMapper.toBookingDto(booking1)
                ));
    }

    @Test
    void createBookingOnNotAvailableItemTest() {
        item1.setAvailable(false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        assertThrows(NotFoundException.class,
                () -> bookingService.create(user1.getId(),
                        BookingMapper.toBookingDto(booking1)));
    }

    @Test
    void createBookingWithWrongTimeTest() {
        booking1.setStart(LocalDateTime.now().minusDays(3));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        assertThrows(NotFoundException.class,
                () -> bookingService.create(user1.getId(),
                        BookingMapper.toBookingDto(booking1)));
    }

    @Test
    void createBookingOnNotExistingItemTest() {
        booking1.setStart(LocalDateTime.now().minusDays(3));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        assertThrows(NotFoundException.class,
                () -> bookingService.create(user1.getId(),
                        BookingMapper.toBookingDto(booking1)));
    }

    @Test
    void createBookingWithWrongUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        assertThrows(NotFoundException.class,
                () -> bookingService.create(1L,
                        BookingMapper.toBookingDto(booking1)));
    }

    @Test
    void changeStatus() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        BookingDtoResponse bookingDtoResponse = bookingService.changeStatus(
                user1.getId(),
                booking1.getId(),
                true);

        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(start, bookingDtoResponse.getStart());
        assertEquals(end, bookingDtoResponse.getEnd());
        assertEquals(item1, bookingDtoResponse.getItem());
        assertEquals(user2, bookingDtoResponse.getBooker());
        assertEquals(BookingStatus.APPROVED, bookingDtoResponse.getStatus());
    }

    @Test
    void updateBookingWithWrongIdTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        assertThrows(NotFoundException.class,
                () -> bookingService.changeStatus(
                        user2.getId(),
                        booking1.getId(),
                        true));
    }

    @Test
    void updateBookingFromWrongUserTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.changeStatus(
                        user2.getId(),
                        booking1.getId(),
                        true));
    }

    @Test
    void changeStatusBookingStatusApprovedTwiceTest() {

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        booking1.setStatus(BookingStatus.APPROVED);

        assertThrows(BadRequestException.class,
                () -> bookingService.changeStatus(
                        user1.getId(),
                        booking1.getId(),
                        true));
    }

    @Test
    void updateBookingRejectTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        BookingDtoResponse bookingDtoResponse = bookingService.changeStatus(
                user1.getId(),
                booking1.getId(),
                false);

        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(start, bookingDtoResponse.getStart());
        assertEquals(end, bookingDtoResponse.getEnd());
        assertEquals(item1, bookingDtoResponse.getItem());
        assertEquals(user2, bookingDtoResponse.getBooker());
        assertEquals(BookingStatus.REJECTED, bookingDtoResponse.getStatus());
    }

    @Test
    void getBookingTest() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        BookingDtoResponse bookingDtoResponse = bookingService.getBookingInfo(
                booking1.getId(),
                user1.getId());

        assertEquals(1, bookingDtoResponse.getId());
        assertEquals(start, bookingDtoResponse.getStart());
        assertEquals(end, bookingDtoResponse.getEnd());
        assertEquals(item1, bookingDtoResponse.getItem());
        assertEquals(user2, bookingDtoResponse.getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponse.getStatus());
    }

    @Test
    void getBookingInfoYouNotABooker() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        booking1.setBooker(user2);

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingInfo(
                        5L,
                        booking1.getId()));
    }

    @Test
    void getBookingsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByBooker(user1.getId(),
                "ALL",
                page);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsCurrentStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerCurrent(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByBooker(user1.getId(),
                "CURRENT",
                page);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsPastStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerPast(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByBooker(user1.getId(),
                "PAST",
                page);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsFutureStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerFuture(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByBooker(user1.getId(),
                "FUTURE",
                page);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsWaitingStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerAndStatus(
                anyLong(),
                any(BookingStatus.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));
        PageRequest page = PageRequest.of(0, 10);


        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByBooker(user1.getId(),
                "WAITING",
                page);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsRejectedStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByBookerAndStatus(
                anyLong(),
                any(BookingStatus.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByBooker(user1.getId(),
                "REJECTED",
                page);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getBookingsUnknownStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        PageRequest page = PageRequest.of(0, 10);

        assertThrows(UnsupportedStateException.class,
                () -> bookingService.getByBooker(user1.getId(),
                        "UNKNOWN",
                        page));
    }

    @Test
    void getBookingsWithWrongUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1);

        booking1.setBooker(user2);

        PageRequest page = PageRequest.of(0, 10);

        assertThrows(NotFoundException.class,
                () -> bookingService.getByBooker(
                        5L,
                        "WAITING",
                        page));
    }

    @Test
    void getItemsOwnerBookingsTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByOwner(user1.getId(),
                "ALL",
                page);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsCurrentStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerCurrent(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByOwner(user1.getId(),
                "CURRENT",
                page);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsPastStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerPast(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByOwner(user1.getId(),
                "PAST",
                page);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsFutureStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerFuture(
                anyLong(),
                any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByOwner(user1.getId(),
                "FUTURE",
                page);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsWaitingStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerAndStatus(
                anyLong(),
                any(BookingStatus.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByOwner(user1.getId(),
                "WAITING",
                page);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsRejectedStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findByItemOwnerAndStatus(
                anyLong(),
                any(BookingStatus.class),
                any(PageRequest.class)))
                .thenReturn(List.of(booking1));

        PageRequest page = PageRequest.of(0, 10);

        List<BookingDtoResponse> bookingDtoResponses = bookingService.getByOwner(user1.getId(),
                "REJECTED",
                page);

        assertEquals(1, bookingDtoResponses.size());
        assertEquals(1, bookingDtoResponses.get(0).getId());
        assertEquals(start, bookingDtoResponses.get(0).getStart());
        assertEquals(end, bookingDtoResponses.get(0).getEnd());
        assertEquals(item1, bookingDtoResponses.get(0).getItem());
        assertEquals(user2, bookingDtoResponses.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookingDtoResponses.get(0).getStatus());
    }

    @Test
    void getItemsOwnerBookingsUnknownStateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));

        PageRequest page = PageRequest.of(0, 10);

        assertThrows(UnsupportedStateException.class,
                () -> bookingService.getByOwner(user1.getId(),
                        "UNKNOWN",
                        page));
    }

    @Test
    void getItemsOwnerWithWrongUser() {

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        PageRequest page = PageRequest.of(0, 10);

        assertThrows(NotFoundException.class,
                () -> bookingService.getByOwner(user1.getId(),
                        "WAITING",
                        page));
    }
}