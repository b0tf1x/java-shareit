drop table if exists USERS;
drop table if exists BOOKINGS;
drop table if exists ITEMS;

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