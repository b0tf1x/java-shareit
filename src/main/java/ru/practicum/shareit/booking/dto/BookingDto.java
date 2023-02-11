package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    @NotNull(groups = Create.class)
    private long id;
    @FutureOrPresent(groups = Create.class)
    private LocalDateTime start;
    @Future(groups = Create.class)
    private LocalDateTime end;
    private long booker;
    @NotNull(groups = Create.class)
    private long itemId;
    private Status status;
}
