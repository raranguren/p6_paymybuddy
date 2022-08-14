/* Setting up database */
DROP DATABASE IF EXISTS prod;
CREATE DATABASE prod;
USE prod;
CREATE TABLE user
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    email    VARCHAR(70) UNIQUE    NOT NULL,
    password VARCHAR(100)          NOT NULL,
    PRIMARY KEY (id)
);
CREATE TABLE wallet
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    user_id            BIGINT,
    billing_details_id BIGINT,
    profile_name       VARCHAR(70)           NOT NULL,
    balance            INT DEFAULT 0,
    PRIMARY KEY (id)
);
CREATE TABLE connections
(
    wallet_id            BIGINT,
    connection_wallet_id BIGINT
);
CREATE TABLE transfer
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    sender_wallet_id   BIGINT,
    receiver_wallet_id BIGINT,
    description        VARCHAR(70)           NOT NULL,
    amount             INT,
    time_completed     TIMESTAMP,
    PRIMARY KEY (id)
);
CREATE TABLE billing_details
(
    id BIGINT AUTO_INCREMENT NOT NULL,
    PRIMARY KEY (id)
);

/* Create TEST DB */
DROP DATABASE IF EXISTS test;
CREATE DATABASE test;

/* Mirror the structure in a database for tests */
USE test;
CREATE TABLE user LIKE prod.user;
CREATE TABLE wallet LIKE prod.wallet;
CREATE TABLE connections LIKE prod.connections;
CREATE TABLE transfer LIKE prod.transfer;
CREATE TABLE billing_details LIKE prod.billing_details;

/* Users to access the databases with limited permissions */
DROP USER IF EXISTS 'dbuser';
DROP USER IF EXISTS 'testuser';
CREATE USER 'dbuser' IDENTIFIED BY 'dbpassword';
CREATE USER 'testuser' IDENTIFIED BY 'testpassword';
GRANT SELECT, INSERT, UPDATE ON prod.* TO 'dbuser';
GRANT DELETE ON prod.connections TO 'dbuser';
GRANT SELECT, INSERT, UPDATE, DELETE ON test.* TO 'testuser';

/* Adding some example data to PROD DB. Passwords are "123" in all cases */
use prod;
SET @password_123 = '$2a$12$W7BAG6324Ft0lR4bRZpPge/OsJUDX9NFyZ/3FVb1UVa4Fn12KU7KG';

insert into user (id, email, password)
values (1, '1@mail.com', @password_123),
       (2, '2@mail.com', @password_123),
       (3, '3@mail.com', @password_123),
       (4, '4@mail.com', @password_123),
       (5, '5@mail.com', @password_123);

insert into wallet (user_id, profile_name, balance)
values (1, 'Ricardo', 1000),
       (2, 'Hayley', 1000),
       (3, 'Clara', 2500),
       (4, 'Smith', 800),
       (5, 'New Friend', 0);

insert into connections (wallet_id, connection_wallet_id)
VALUES (1, 2),
       (1, 3),
       (1, 4);

insert into transfer (sender_wallet_id, receiver_wallet_id, description, amount)
VALUES (1, 2, 'Restaurant bill share', 1000),
       (1, 3, 'Trip money', 2500),
       (1, 4, 'Movie tickets', 800),
       (1, 4, 'And 20 cents', 20);