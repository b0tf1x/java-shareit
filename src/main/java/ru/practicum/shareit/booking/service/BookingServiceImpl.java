package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
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
import java.util.ArrayList;
import java.util.List;

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
    public Booking create(long userId, BookingDto bookingDto) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> {
            throw new NotFoundException("Вещь не найдена");
        });
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
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, item, user));
        log.info(booking.toString());
        log.info("bookerId = " + booking.getBooker().getId() + " bookingId = " + booking.getId());
        log.info("itemId  = " + item.getId());
        return booking;
    }

    @Transactional
    @Override
    public Booking updateStatus(long userId, long bookingId, boolean approved) {
        log.info("update Status");
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Вещь не найдена");
        });
        Item item = booking.getItem();
        if (userId != item.getOwner().getId()) {
            throw new NotFoundException("Нельзя редактировать статус не своей вещи");
        }
        if (booking.getStatus() == Status.APPROVED) {
            throw new FailException("Нельзя редактировать статус после подтверждения");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingInformation(long userId, long bookingId) {
        log.info("get Booking information");
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Вещь не найдена");
        });
        Item item = booking.getItem();
        if (booking.getBooker().getId() == userId || item.getOwner().getId() == userId) {
            return booking;
        } else throw new NotFoundException("Нельзя получить информацию");
    }

    @Override
    public List<Booking> getByBooker(long userId, String state, int from, int size) {
        log.info("get By Booker");
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Booking> books = new ArrayList<>();
        switch (state) {
            case "ALL":
                books.addAll(bookingRepository.findAllByBookerOrderByStartDesc(userId, pageRequest));
                break;
            case "CURRENT":
                books.addAll(bookingRepository.findByBookerCurrent(userId, LocalDateTime.now(), pageRequest));
                break;
            case "PAST":
                books.addAll(bookingRepository.findByBookerPast(userId, LocalDateTime.now(), pageRequest));
                break;
            case "FUTURE":
                books.addAll(bookingRepository.findByBookerFuture(userId, LocalDateTime.now(), pageRequest));
                break;
            case "WAITING":
                books.addAll(bookingRepository.findByBookerAndState(userId, Status.WAITING, pageRequest));
                break;
            case "REJECTED":
                books.addAll(bookingRepository.findByBookerAndState(userId, Status.REJECTED, pageRequest));
                break;
            default:
                throw new UnsupportedStateException("Неправильный state");
        }
        return books;
    }


    @Override
    public List<Booking> getByOwner(long userId, String state, int from, int size) {
        log.info("get by owner");
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Booking> books = new ArrayList<>();
        switch (state) {
            case "ALL":
                books.addAll(bookingRepository.getByOwner(userId, pageRequest));
                break;
            case "CURRENT":
                books.addAll(bookingRepository.getByOwnerCurrent(userId, LocalDateTime.now(), pageRequest));
                break;
            case "PAST":
                books.addAll(bookingRepository.getByOwnerPast(userId, LocalDateTime.now(), pageRequest));
                break;
            case "FUTURE":
                books.addAll(bookingRepository.getByOwnerFuture(userId, LocalDateTime.now(), pageRequest));
                break;
            case "WAITING":
                books.addAll(bookingRepository.findByOwnerAndState(userId, Status.WAITING, pageRequest));
                break;
            case "REJECTED":
                books.addAll(bookingRepository.findByOwnerAndState(userId, Status.REJECTED, pageRequest));
                break;
            default:
                throw new UnsupportedStateException("Неправильный state");
        }
        return books;
    }
}
