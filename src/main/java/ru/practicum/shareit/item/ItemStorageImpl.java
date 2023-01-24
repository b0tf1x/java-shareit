package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
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
        ItemDto itemDto = new ItemDto();
        for (long id : items.keySet()) {
            List<Item> userItem = items.get(id);
            for (Item item : userItem) {
                if (item.getId() == itemId) {
                    itemDto = ItemMapper.toItemDto(item);
                }
            }
        }
        log.info("Предмет успешно найден");
        return itemDto;
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        itemDto.setId(currentId++);
        Item item = ItemMapper.toItem(itemDto, userId);
        List<Item> userItems = items.get(userId);
        if (userItems == null) {
            userItems = new ArrayList<>();
        }
        userItems.add(item);
        items.put(userId, userItems);
        log.info("Предмет успешно создан");
        return itemDto;
    }

    @Override
    public ItemDto put(long userId, ItemDto itemDto, long itemId) {
        if (items.get(userId) == null) {
            throw new NotFoundException("Вещь не найдена");
        }
        Item item = items.get(userId).stream()
                .filter(item1 -> item1.getId() == itemId)
                .findFirst().orElseThrow(() -> {
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
        List<ItemDto> result = new ArrayList<>();
        for (long userId : items.keySet()) {
            List<Item> userItem = items.get(userId);
            for (Item item : userItem) {
                if ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) && item.getAvailable().equals(true)) {
                    result.add(ItemMapper.toItemDto(item));
                }
            }
        }
        return result;
    }
}
