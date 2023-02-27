package ru.practicum.shareit.comment.dto;

import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> jacksonTester;
    private CommentDto commentDto;
    private Comment comment;
    private User user1;
    private User user2;
    private Item item;

    @BeforeEach
    void start(){
        user1 = new User(1L,"name1","email1@mail.com");
        user2 = new User(1L,"name2","email2@mail.com");
        item = new Item(1L,"name","description",true,user1,null);
        comment = new Comment(1L,"text",item,user2);
        commentDto = CommentMapper.toCommentDto(comment);
    }
    @Test
    void testJson() throws Exception{
        JsonContent<CommentDto> json = jacksonTester.write(commentDto);
        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(Math.toIntExact(commentDto.getId()));
        assertThat(json).extractingJsonPathStringValue("$.text")
                .isEqualTo(commentDto.getText());
        assertThat(json).extractingJsonPathStringValue("$.authorName")
                .isEqualTo(commentDto.getAuthorName());
    }
}
