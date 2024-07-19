--liquibase formatted sql

--changeset Dmitry Smirnov:1_4
--comment: Создание таблицы боксов

--Боксы
CREATE TABLE IF NOT EXISTS boxes
(
    id          SERIAL PRIMARY KEY,
    start       TIME NOT NULL,
    finish      TIME NOT NULL,
    usage_rate  REAL NOT NULL,
    operator_id INT  NOT NULL,
    FOREIGN KEY (operator_id) REFERENCES users (id)
);