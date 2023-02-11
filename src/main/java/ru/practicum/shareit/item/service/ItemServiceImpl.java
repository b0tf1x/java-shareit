package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> findAll(long userId) {
        return itemRepository.findAllByOwnerIdOrderById(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto findItemById(long itemId) {
        return itemRepository.findById(itemId).map(ItemMapper::toItemDto).orElseThrow(() -> {
            throw new NotFoundException("Вещь не найдена");
        });
    }

    @Override
    @Transactional
    public ItemDto create(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователь не найден");
        });
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user));
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto put(long userId, ItemDto itemDto, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Вещь не найдена");
        });
        if (item.getOwner().getId() == userId) {
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            itemRepository.save(item);
        } else {
            throw new NotFoundException("Ошибка при обновлении вещи");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text.toLowerCase())
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
