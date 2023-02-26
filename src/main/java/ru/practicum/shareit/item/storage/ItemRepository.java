package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderByIdAsc(long userId);

    @Query("select item from Item item " +
            "where item.available = true " +
            "and (lower(item.name) like %?1% " +
            "or lower(item.description) like %?1%)")
    List<Item> search(String text);
    @Query("select item from Item item " +
            "where item.itemRequest.id in ?1 ")
    List<Item> findByRequestsIds(List<Long> requestsIds);
    List<Item>findByItemRequestId(long requestId);
}
