package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {

    private final CommentRepository commentRepository;

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final ItemRequestRepository requestRepository;

    @Override
    @Transactional
    public ItemDto create(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = requestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new NotFoundException("Запрос не найден"));
        }
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user, itemRequest));
        itemDto.setId(item.getId());
        return itemDto;
    }

    @Override
    @Transactional
    public ItemDto put(long userId, long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Вещь для обновления не найдена");
        });
        if (item.getOwner().getId() == userId) {
            if (itemDto.getName() != null) item.setName(itemDto.getName());
            if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
            if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
            itemRepository.save(item);
        } else {
            throw new NotFoundException("Вещь для обновления не найдена");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDtoBooking> findAll(long userId, Pageable page) {
        return setAllBookingsAndComments(userId, itemRepository.findAllByOwnerIdOrderByIdAsc(userId, page));
    }


    @Override
    public ItemDtoBooking findItem(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Вещь для обновления не найдена");
        });
        return setAllBookingsAndComments(userId, Collections.singletonList(item)).get(0);
    }

    @Override
    public List<ItemDto> searchItem(String text, Pageable page) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchByText(text.toLowerCase(), page)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("Вы не можете комментировать эту вещь"));
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = CommentMapper.toComment(user, item, commentDto);
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    private List<ItemDtoBooking> setAllBookingsAndComments(long userId, List<Item> items) {
        List<Long> ids = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findBookingsLast(ids, LocalDateTime.now(), userId);
        Map<Long, ItemDtoBooking> itemsMap = items.stream()
                .map(ItemMapper::toItemDtoBooking)
                .collect(Collectors.toMap(ItemDtoBooking::getId, item -> item, (a, b) -> b));
        bookings.forEach(booking -> itemsMap.get(booking.getItem().getId())
                .setLastBooking(BookingMapper.toBookingDto(booking)));
        bookings = bookingRepository.findBookingsNext(ids, LocalDateTime.now(), userId);
        bookings.forEach(booking -> itemsMap.get(booking.getItem().getId())
                .setNextBooking(BookingMapper.toBookingDto(booking)));
        List<Comment> comments = commentRepository.findAllComments(ids);
        comments.forEach(comment -> itemsMap.get(comment.getItem().getId())
                .getComments().add(CommentMapper.toCommentDto(comment)));
        return new ArrayList<>(itemsMap.values());
    }
}
