package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.storage.dto.ItemRequestDto;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.Variables.USER_HEADER;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_HEADER) long userId,
                                 @Validated @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsInformation(@RequestHeader(USER_HEADER) long userId) {
        return itemRequestService.getRequestsInformation(userId);
    }

    @GetMapping("/{request}")
    public ItemRequestDto getRequestInformation(@RequestHeader(USER_HEADER) long userId,
                                                @PathVariable long request) {
        return itemRequestService.getRequestInformation(userId, request);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_HEADER) long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                               @Positive @RequestParam(defaultValue = "10", required = false) int size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }
}
