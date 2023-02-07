package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
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
    List<Booking> findByBookerPast(long userId, LocalDateTime endTime);
    List<Booking> findByBookerFuture(long userId, LocalDateTime startTime);
}
