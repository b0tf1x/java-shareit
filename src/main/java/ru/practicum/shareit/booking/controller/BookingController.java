package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingController {
    private final BookingService bookingService;
    private static final String userHeader = "X-Sharer-User-Id";

    @PostMapping
    public Booking create(@RequestHeader(userHeader) long userId,
                          @Validated(Create.class) @RequestBody BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking updateStatus(@RequestHeader(userHeader) long userId,
                                @PathVariable long bookingId,
                                @RequestParam boolean approved) {
        return bookingService.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingInformation(@RequestHeader(userHeader) long userId,
                                         @PathVariable long bookingId) {
        return bookingService.getBookingInformation(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getByBooker(@RequestHeader(userHeader) long userId,
                                     @RequestParam(defaultValue = "ALL") String state,
                                     @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                     @Positive @RequestParam(defaultValue = "10", required = false) int size) {
        return bookingService.getByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<Booking> getByOwner(@RequestHeader(userHeader) long userId,
                                    @RequestParam(defaultValue = "ALL") String state,
                                    @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                    @Positive @RequestParam(defaultValue = "10", required = false) int size) {
        return bookingService.getByOwner(userId, state, from, size);
    }
}
