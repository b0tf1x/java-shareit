package shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b " +
            "where b.booker.id = ?1 " +
            "ORDER BY b.start desc")
    List<Booking> findAllByBookerOrderByStartDesc(long userId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.start < ?2 " +
            "AND b.end> ?2 " +
            "AND b.booker.id = ?1 " +
            "ORDER BY b.start")
    List<Booking> findByBookerCurrent(long userId, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.end < ?2 " +
            "AND b.booker.id = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> findByBookerPast(long userId, LocalDateTime endTime, Pageable pageable);

    @Query("SELECT b from Booking b " +
            "WHERE b.start >?2 " +
            "AND b.booker.id = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> findByBookerFuture(long userId, LocalDateTime startTime, Pageable pageable);

    @Query("SELECT b from Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.status desc")
    List<Booking> findByBookerAndState(long userId, Status status, Pageable pageable);

    @Query("SELECT b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.end > ?2 " +
            "and b.start < ?2 " +
            "order by b.start")
    List<Booking> getByOwnerCurrent(long userId, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> getByOwnerPast(long userId, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "AND b.start > ?2 " +
            "ORDER BY b.start desc")
    List<Booking> getByOwnerFuture(long userId, LocalDateTime currentTime, Pageable pageable);

    @Query("SELECT b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "order by b.start desc")
    List<Booking> getByOwner(long userId, Pageable pageable);

    @Query("SELECT b from Booking b " +
            "where b.item.owner.id = ?1 " +
            "AND b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findByOwnerAndState(long userId, Status status, Pageable pageable);

    @Query("select distinct b from Booking b " +
            "where b.end < ?2 " +
            "and b.item.id = ?1 " +
            "and b.item.owner.id = ?3 " +
            "order by b.start desc")
    Optional<Booking> findLastBooking(long itemId, LocalDateTime now, long userId);

    @Query("select distinct b from Booking b " +
            "where b.start > ?2 " +
            "and b.item.id = ?1 " +
            "and b.item.owner.id = ?3 " +
            "order by b.start ")
    Optional<Booking> findNextBooking(long itemId, LocalDateTime now, long userId);

    Optional<Booking> findByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime end);
}
