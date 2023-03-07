package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.Create;
import ru.practicum.shareit.booking.Put;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {
    private final UserClient userClient;
    @GetMapping
    public ResponseEntity<Object>findAll(){
        return userClient.findAll();
    }
    @GetMapping("/{userId}")
    public ResponseEntity<Object>findUserById(@PathVariable long userId){
        return userClient.findUserById(userId);
    }
    @PostMapping
    public ResponseEntity<Object> create(@Validated(Create.class)UserDto userDto){
        return userClient.create(userDto);
    }
    @PatchMapping("/{userId}")
    public ResponseEntity<Object>put(@PathVariable long userId, @Validated(Put.class) @RequestBody UserDto userDto){
        return userClient.put(userId,userDto);
    }
    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId){
        userClient.delete(userId);
    }
}
