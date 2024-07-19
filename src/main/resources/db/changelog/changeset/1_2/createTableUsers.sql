--liquibase formatted sql

--changeset Dmitry Smirnov:1_2
--comment: Создание таблицы пользователей

--Пользователи
CREATE TABLE IF NOT EXISTS users
(
    id        SERIAL PRIMARY KEY,
    login     VARCHAR(250) NOT NULL,
    password  VARCHAR(250) NOT NULL,
    name      VARCHAR(200) NOT NULL,
    email     VARCHAR(200) NOT NULL,
    role_name VARCHAR(200) NOT NULL,
    discount  INT          NOT NULL DEFAULT 0,
    is_delete BOOLEAN      NOT NULL DEFAULT false,
    FOREIGN KEY (role_name) REFERENCES roles (name)
);