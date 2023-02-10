package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.storage.UserRepository;

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

    @Override
    @Transactional
    public BookingDto create(long userId, BookingDto bookingDto) {
        log.info("item = "+bookingDto.getItemId());
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> {
            throw new NotFoundException("Вещь не найдена");
        });
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        if (item.getOwner() == userId) {
            throw new ValidationException("Нельзя заказать вещь у себя");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Нельзя забронировать вещь");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Конец брони до её начала");
        }
        bookingDto.setStatus(Status.WAITING);
        bookingRepository.save(BookingMapper.toBooking(bookingDto));
        return bookingDto;
    }

    @Transactional
    @Override
    public BookingDto updateStatus(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Вещь не найдена");
        });
        Item item = itemRepository.findById(booking.getItem()).orElseThrow(() -> {
            throw new NotFoundException("Вещь не найдена");
        });
        if (userId != item.getOwner()) {
            throw new ValidationException("Нельзя редактировать статус не своей вещи");
        }
        if (booking.getStatus() == Status.APPROVED) {
            throw new ValidationException("Нельзя редактировать статус после подтверждения");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDto(booking);
    }

    public BookingDto getBookingInformation(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Вещь не найдена");
        });
        Item item = itemRepository.findById(booking.getItem()).orElseThrow(() -> {
            throw new NotFoundException("Вещь не найдена");
        });
        if (booking.getBooker() == userId || item.getOwner() == userId) {
            return BookingMapper.toBookingDto(booking);
        } else throw new ValidationException("Нельзя получить информацию");
    }

    public List<BookingDto> getByBooker(long userId, String state) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        return getBooks(userId,state);
    }

    public List<BookingDto> getBooks(long userId, String state) {
        List<Booking> books = new ArrayList<>();
        switch (state) {
            case "CURRENT":
                books.addAll(bookingRepository.findByBookerCurrent(userId, LocalDateTime.now()));
                break;
            case "PAST":
                books.addAll(bookingRepository.findByBookerPast(userId,LocalDateTime.now()));
                break;
            case "FUTURE":
                books.addAll(bookingRepository.findByBookerFuture(userId,LocalDateTime.now()));
                break;
            case "WAITING":
                books.addAll(bookingRepository.findByBookerAndState(userId,Status.WAITING));
                break;
            case "REJECTED":
                books.addAll(bookingRepository.findByBookerAndState(userId,Status.REJECTED));
                break;
            default:
                books.addAll(bookingRepository.findAllByBookerOrderByStartDesc(userId));
        }
        return books.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getByOwner(long userId, String state){
        List<Booking> books = new ArrayList<>();
        switch (state) {
            case "CURRENT":
                books.addAll(bookingRepository.getByOwnerCurrent(userId,LocalDateTime.now()));
                break;
            case "PAST":
                books.addAll(bookingRepository.getByOwnerPast(userId,LocalDateTime.now()));
                break;
            case "FUTURE":
                books.addAll(bookingRepository.getByOwnerFuture(userId,LocalDateTime.now()));
                break;
            case "WAITING":
                books.addAll(bookingRepository.findByOwnerAndState(userId,Status.WAITING));
                break;
            case "REJECTED":
                books.addAll(bookingRepository.findByOwnerAndState(userId,Status.REJECTED));
                break;
            default:
            books.addAll(bookingRepository.getByOwner(userId));
        }
        return books.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
