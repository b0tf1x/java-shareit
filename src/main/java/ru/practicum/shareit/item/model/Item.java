package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ITEMS")
public class Item {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long owner;
    private long request;

    public Item(long id, String name, String description, Boolean available, long owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
    }
}
