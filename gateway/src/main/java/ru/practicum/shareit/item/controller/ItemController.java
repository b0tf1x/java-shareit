package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.booking.Create;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemDto;

import static ru.practicum.shareit.ShareItGateway.USER_HEADER;

@RestController
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_HEADER) long userId,
                                         @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> put(@RequestHeader(USER_HEADER) long userId,
                                      @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        return itemClient.put(userId, itemId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(USER_HEADER) long userId) {
        return itemClient.findAll(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader(USER_HEADER) long userId,
                                               @PathVariable long itemId) {
        return itemClient.findItemById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(USER_HEADER) long userId,
                                         @RequestParam String search) {
        return itemClient.search(userId, search);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_HEADER) long userId,
                                             @PathVariable long itemId,
                                             @Validated(Create.class) @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
