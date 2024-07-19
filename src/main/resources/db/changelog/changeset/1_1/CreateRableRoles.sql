--liquibase formatted sql

--changeset Dmitry Smirnov:1_1
--comment: Создание таблицы ролей

--Роли
CREATE TABLE IF NOT EXISTS roles
(
    name VARCHAR(250) NOT NULL PRIMARY KEY
);