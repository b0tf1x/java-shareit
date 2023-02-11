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
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.Create;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingController {
    private final BookingService bookingService;
    private static final String userHeader = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestHeader(userHeader) long userId,
                             @Validated(Create.class) @RequestBody BookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateStatus(@RequestHeader(userHeader) long userId,
                                   @PathVariable long bookingId,
                                   @RequestParam boolean approved) {
        return bookingService.updateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingInformation(@RequestHeader(userHeader) long userId,
                                            @PathVariable long bookingId) {
        return bookingService.getBookingInformation(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getByBooker(@RequestHeader(userHeader) long userId,
                                        @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwner(@RequestHeader(userHeader) long userId,
                                       @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getByOwner(userId, state);
    }
}
