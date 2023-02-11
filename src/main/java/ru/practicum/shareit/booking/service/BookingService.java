package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(long id, BookingDto bookingDto);

    BookingDto updateStatus(long userId, long bookingId, boolean approved);

    BookingDto getBookingInformation(long userId, long bookingId);

    List<BookingDto> getByBooker(long userId, String status);

    List<BookingDto> getByOwner(long userId, String status);
}
