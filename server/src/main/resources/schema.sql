CREATE TABLE IF NOT EXISTS USERS (
    id BIGINT AUTO_INCREMENT primary key,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) unique NOT NULL
    );
CREATE TABLE IF NOT EXISTS ITEMS (
    id BIGINT AUTO_INCREMENT primary key,
    name VARCHAR(256) NOT NULL,
    description VARCHAR(512) NOT NULL,
    available BOOLEAN NOT NULL,
    owner BIGINT not null,
    CONSTRAINT fk_user
    FOREIGN KEY(owner)
    REFERENCES users(id)
    );
CREATE TABLE IF NOT EXISTS BOOKINGS(
    id BIGINT AUTO_INCREMENT primary key,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    itemId BIGINT not null,
    bookerId BIGINT not null,
    status VARCHAR(64) not null,
    CONSTRAINT fk_item
    FOREIGN KEY(itemId)
    REFERENCES items(id),
    CONSTRAINT fk_booker
    FOREIGN KEY(bookerId)
    REFERENCES users(id)
    );
CREATE TABLE IF NOT EXISTS COMMENTS
(
    id    BIGINT AUTO_INCREMENT primary key,
    text     VARCHAR not null,
    item_id   BIGINT  not null,
    author_id BIGINT  not null,
    CONSTRAINT fk_item_comment
    FOREIGN KEY (item_id)
    REFERENCES items (id),
    CONSTRAINT fk_author
    FOREIGN KEY (author_id)
    REFERENCES users (id)
    );
create table if not exists requests(
    id bigint auto_increment primary key,
    requestorId bigint not null,
    description varchar(256) not null,
    created timestamp without time zone not null,
    constraint fk_requestor
    foreign key (requestorId)
    references users(id)
    );
