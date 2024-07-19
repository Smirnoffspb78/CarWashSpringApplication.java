--liquibase formatted sql

--changeset Dmitry Smirnov:1_3
--comment: Создание таблицы работников

--Работники
CREATE table IF NOT EXISTS employees
(
    id                    INT PRIMARY KEY,
    min_discount_for_user INT NOT NULL DEFAULT 0,
    max_discount_for_user INT NOT NULL DEFAULT 0,
    FOREIGN KEY (id) REFERENCES users (id)
);