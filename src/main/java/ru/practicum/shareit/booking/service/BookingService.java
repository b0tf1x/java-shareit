package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking create(long id, BookingDto bookingDto);

    Booking updateStatus(long userId, long bookingId, boolean approved);

    Booking getBookingInformation(long userId, long bookingId);

    List<Booking> getByBooker(long userId, String status);

    List<Booking> getByOwner(long userId, String status);
}
