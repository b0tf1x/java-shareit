package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderByIdAsc(long userId, Pageable pageRequest);

    @Query("select item from Item item " +
            "where item.available = true " +
            "and (lower(item.name) like %?1% " +
            "or lower(item.description) like %?1%)")
    List<Item> searchByText(String text, Pageable pageRequest);

    @Query("select item from Item item " +
            "where item.itemRequest.id in ?1 ")
    List<Item> findByRequestsIds(List<Long> requestsIds);

    List<Item> findByItemRequestId(long requestId);

}
