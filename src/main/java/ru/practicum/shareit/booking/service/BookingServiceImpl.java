package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor = @Autowired)
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDto create(long id, BookingDto bookingDto) {
        Item item = itemRepository.findById(bookingDto.getItem()).orElseThrow(() -> {
            throw new NotFoundException("Вещь не найдена");
        });
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        if (item.getOwner() == id) {
            throw new ValidationException("Нельзя заказать вещь у себя");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Нельзя забронировать вещь");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Конец брони до её начала");
        }
        bookingDto.setStatus(Status.WAITING);
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
        List<BookingDto> books = new ArrayList<>();
    }

    public List<BookingDto> getBooks(long userId, String state) {
        List<BookingDto> books = new ArrayList<>();
        switch (state) {
            case "CURRENT":
                bookingRepository.findByBookerCurrent(userId, LocalDateTime.now());
                break;
            case "PAST":
                break;
            case "FUTURE":
                break;
            case "WAITING":
                break;
            case "REJECTED":
                break;
            default:
                bookingRepository.findAllByBookerOrderByStartDesc(userId);
        }
    }

    public List<BookingDto> getByOwner(long userId, String status);
}
