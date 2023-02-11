package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, List<Item>> items = new HashMap<>();
    private long currentId = 1;

    @Override
    public List<ItemDto> findAll(long userId) {
        List<Item> userItems = items.get(userId);
        return userItems.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto findItemById(long itemId) {
        List<Item> itemsList = new ArrayList<>();
        items.forEach((user, items1) -> itemsList.addAll(items1));
        return itemsList.stream()
                .filter(item1 -> item1.getId() == itemId)
                .findFirst().map(ItemMapper::toItemDto)
                .orElse(new ItemDto());
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        itemDto.setId(currentId++);
        List<Item> userItems = items.get(userId);
        if (userItems == null) {
            userItems = new ArrayList<>();
        }
        items.put(userId, userItems);
        log.info("Предмет успешно создан");
        return itemDto;
    }

    @Override
    public ItemDto put(long userId, ItemDto itemDto, long itemId) {
        if (items.get(userId) == null) {
            throw new NotFoundException("Вещь не найдена");
        }
        Item item = items.get(userId).stream().filter(item1 -> item1.getId() == itemId).findFirst().orElseThrow(() -> {
            throw new NotFoundException("Вещь не найдена");
        });
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        items.get(userId).removeIf(item1 -> item1.getId() == itemId);
        items.get(userId).add(item);
        log.info("Предмет успешно добавлен");
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> itemsList = new ArrayList<>();
        items.forEach((user, items1) -> itemsList.addAll(items1));
        return itemsList.stream()
                .filter(item1 -> item1.getName().toLowerCase().contains(text.toLowerCase())
                        || item1.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
