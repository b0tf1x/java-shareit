package ru.practicum.shareit.comment.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private Item item;
    private Comment comment;

    @BeforeEach
    void start() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        commentRepository.deleteAll();
        User user1 = new User(1L, "name1", "email1@mail.com");
        user1 = userRepository.save(user1);
        User user2 = new User(2L, "name2", "email2@mail.com");
        user2 = userRepository.save(user2);
        item = new Item(1L, "name", "description", true, user1, null);
        item = itemRepository.save(item);
        comment = new Comment(1L, "text", item, user2);
        comment = commentRepository.save(comment);
    }

    @AfterEach
    void delete() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        commentRepository.deleteAll();
    }

    /**@Test
    void findAllByItemId() {
        List<Comment> commentList = commentRepository.findAllByItemId(item.getId());
        assertEquals(1, commentList.size());
        assertEquals(comment.getItem().getName(), commentList.get(0).getItem().getName());
    }**/
}
