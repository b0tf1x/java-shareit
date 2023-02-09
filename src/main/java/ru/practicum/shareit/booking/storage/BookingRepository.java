package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerOrderByStartDesc(long userId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.start < ?2 " +
            "AND b.end>?2 " +
            "AND b.booker = ?1 " +
            "ORDER BY b.start")
    List<Booking> findByBookerCurrent(long userId, LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.start < ?2 " +
            "AND b.booker = ?1" +
            "ORDER BY b.start DESC")
    List<Booking> findByBookerPast(long userId, LocalDateTime endTime);
    @Query("SELECT b from Booking b "+
    "WHERE b.start >?2 "+
    "AND b.booker = ?1 "+
    "ORDER BY b.start DESC")
    List<Booking> findByBookerFuture(long userId, LocalDateTime startTime);
    @Query("SELECT b from Booking b "+
    "WHERE b.booker = ?1 "+
    "AND b.status = ?2 "+
    "ORDER BY b.status desc")
    List<Booking> findByBookerAndState(long userId, Status status);
    @Query("SELECT b from Booking b " +
            "JOIN Item i on i.id=b.item " +
            "where i.owner = ?1 " +
            "and b.end > ?2 " +
            "and b.start > ?2 " +
            "order by b.start")
    List<Booking> getByOwnerCurrent(long userId,LocalDateTime currentTime);
    @Query("SELECT b from Booking b " +
            "JOIN Item i on i.id=b.item " +
            "where i.owner = ?1 " +
            "and b.end < ?2 " +
            "order by b.start")
    List<Booking> getByOwnerPast(long userId,LocalDateTime currentTime);
    @Query("SELECT b from Booking b " +
            "JOIN Item i on i.id=b.item " +
            "WHERE i.owner = ?1 " +
            "AND b.start > ?2 " +
            "ORDER BY b.start")
    List<Booking> getByOwnerFuture(long userId,LocalDateTime currentTime);
    @Query("SELECT b from Booking b " +
            "JOIN Item i on i.id=b.item " +
            "where i.owner = ?1 " +
            "order by b.start")
    List<Booking>getByOwner(long userId);
    @Query("SELECT b from Booking b " +
            "JOIN Item i on i.id=b.item " +
            "where i.owner = ?1 " +
            "AND b.status = ?2" +
            "order by b.start")
    List<Booking>findByOwnerAndState(long userId, Status status);
}
