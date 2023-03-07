package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.ShareItGateway.USER_HEADER;

@RestController
@RequestMapping(path = "/requests")
@Validated
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_HEADER) long userId,
                                         @Validated @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsInformation(@RequestHeader(USER_HEADER) long userId) {
        return itemRequestClient.getRequestsInformation(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestInformation(@RequestHeader(USER_HEADER) long userId,
                                                        @PathVariable long requestId) {
        return itemRequestClient.getRequestInformation(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_HEADER) long userId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                                 @Positive @RequestParam(defaultValue = "10", required = false) int size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }
}
