package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.FailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Override
    @Transactional
    public BookingDto create(long userId, BookingDto bookingDto) {
        log.info("item = " + bookingDto.getItemId());
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> {
            throw new NotFoundException("Вещь не найдена");
        });
        log.info("ownerId = " + item.getOwner());
        log.info("available = " + item.getAvailable());
        log.info("start = " + bookingDto.getStart() + " end = " + bookingDto.getEnd());
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Не тот пользователь");
        });
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Нельзя заказать вещь у себя");
        }
        if (!item.getAvailable()) {
            throw new FailException("Нельзя забронировать вещь");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new FailException("Конец брони до её начала");
        }
        bookingDto.setStatus(Status.WAITING);
        log.info("id = " + bookingDto.getId());
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, item, user));
        log.info("bookingId = " + booking.getId());
        log.info(booking.toString());
        return BookingMapper.toBookingDto(booking);
    }

    @Transactional
    @Override
    public BookingDto updateStatus(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Вещь не найдена");
        });
        Item item = booking.getItem();
        if (userId != item.getOwner().getId()) {
            throw new ValidationException("Нельзя редактировать статус не своей вещи");
        }
        if (booking.getStatus() == Status.APPROVED) {
            throw new FailException("Нельзя редактировать статус после подтверждения");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getBookingInformation(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Вещь не найдена");
        });
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> {
            throw new NotFoundException("Вещь не найдена");
        });
        if (booking.getBooker().getId() == userId || item.getOwner().getId() == userId) {
            return BookingMapper.toBookingDto(booking);
        } else throw new NotFoundException("Нельзя получить информацию");
    }

    @Override
    public List<BookingDto> getByBooker(long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        List<Booking> books = new ArrayList<>();
        switch (state) {
            case "ALL":
                books.addAll(bookingRepository.findAllByBookerOrderByStartDesc(userId));
                break;
            case "CURRENT":
                books.addAll(bookingRepository.findByBookerCurrent(userId, LocalDateTime.now()));
                break;
            case "PAST":
                books.addAll(bookingRepository.findByBookerPast(userId, LocalDateTime.now()));
                break;
            case "FUTURE":
                books.addAll(bookingRepository.findByBookerFuture(userId, LocalDateTime.now()));
                break;
            case "WAITING":
                books.addAll(bookingRepository.findByBookerAndState(userId, Status.WAITING));
                break;
            case "REJECTED":
                books.addAll(bookingRepository.findByBookerAndState(userId, Status.REJECTED));
                break;
            default:
                throw new FailException("Неправильный state");
        }
        return books.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<BookingDto> getByOwner(long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        List<Booking> books = new ArrayList<>();
        switch (state) {
            case "ALL":
                books.addAll(bookingRepository.getByOwner(userId));
                break;
            case "CURRENT":
                books.addAll(bookingRepository.getByOwnerCurrent(userId, LocalDateTime.now()));
                break;
            case "PAST":
                books.addAll(bookingRepository.getByOwnerPast(userId, LocalDateTime.now()));
                break;
            case "FUTURE":
                books.addAll(bookingRepository.getByOwnerFuture(userId, LocalDateTime.now()));
                break;
            case "WAITING":
                books.addAll(bookingRepository.findByOwnerAndState(userId, Status.WAITING));
                break;
            case "REJECTED":
                books.addAll(bookingRepository.findByOwnerAndState(userId, Status.REJECTED));
                break;
            default:
                throw new FailException("Неправильный state");
        }
        return books.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
