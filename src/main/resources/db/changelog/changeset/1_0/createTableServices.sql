--liquibase formatted sql

--changeset Dmitry Smirnov:1_0
--comment: Создание таблицы услуг

--Услуги
CREATE TABLE IF NOT EXISTS services
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(250) NOT NULL UNIQUE,
    price NUMERIC      NOT NULL,
    time  INT
);