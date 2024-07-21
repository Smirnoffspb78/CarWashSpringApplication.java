--liquibase formatted sql

--changeset Dmitry Smirnov:1_5
--comment: Создание таблицы записей

--Записи
CREATE TABLE IF NOT EXISTS records
(
    id          SERIAL PRIMARY KEY,
    user_id     INT       NOT NULL,
    start       TIMESTAMP NOT NULL,
    finish      TIMESTAMP NOT NULL,
    created     TIMESTAMP NOT NULL,
    is_remove   BOOLEAN   NOT NULL DEFAULT false,
    is_reserve  BOOLEAN   NOT NULL DEFAULT false,
    is_arrive   BOOLEAN   NOT NULL DEFAULT false,
    is_complete BOOLEAN   NOT NULL DEFAULT false,
    cost        NUMERIC   NOT NULL,
    box_id      INTEGER   NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (box_id) REFERENCES boxes (id)
);