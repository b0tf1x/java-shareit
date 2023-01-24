package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.booking.dto.Status;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long item;
    private long booker;
    private Status status;
}
