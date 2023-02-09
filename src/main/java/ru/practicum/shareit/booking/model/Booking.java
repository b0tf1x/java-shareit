package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.Status;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="BOOKINGS")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="start_time")
    private LocalDateTime start;
    @Column(name="end_time")
    private LocalDateTime end;
    private long item;
    private long booker;
    @Enumerated(EnumType.STRING)
    private Status status;
}
