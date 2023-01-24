package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public List<ItemDto> findAll(long userId) {
        return itemStorage.findAll(userId);
    }

    @Override
    public ItemDto findItemById(long itemId) {
        return itemStorage.findItemById(itemId);
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        if (userStorage.findUserById(userId) == null) {
            throw new NotFoundException("пользователь не найден");
        }
        return itemStorage.create(userId, itemDto);
    }

    @Override
    public ItemDto put(long userId, ItemDto itemDto, long itemId) {
        if (userStorage.findUserById(userId) == null) {
            throw new NotFoundException("пользователь не найден");
        }
        return itemStorage.put(userId, itemDto, itemId);
    }

    @Override
    public List<ItemDto> search(String text) {
        return itemStorage.search(text);
    }

}
