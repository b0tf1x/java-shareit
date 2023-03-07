package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.Create;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.FailException;
import ru.practicum.shareit.exception.UnsupportedStateException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.ShareItGateway.USER_HEADER;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getByBooker(@RequestHeader(USER_HEADER) long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedStateException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getByBooker(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_HEADER) long userId,
                                         @Validated(Create.class) @RequestBody BookingDto bookingDto) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new FailException("Время начала позже конца");
        }
        return bookingClient.create(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingInformation(@RequestHeader(USER_HEADER) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingInformation(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@RequestHeader(USER_HEADER) long userId,
                                               @PathVariable long bookingId,
                                               @RequestParam boolean approved) {
        return bookingClient.updateStatus(userId, bookingId, approved);
    }
}
