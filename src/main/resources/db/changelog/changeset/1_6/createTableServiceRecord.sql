--liquibase formatted sql

--changeset Dmitry Smirnov:1_6
--comment: Создание таблицы связи услуг и записей

--Связь услуг и записей
CREATE TABLE IF NOT EXISTS service_record
(
    service_id integer NOT NULL,
    record_id  integer NOT NULL,
    PRIMARY KEY (record_id, service_id),
    FOREIGN KEY (service_id) REFERENCES services (id),
    FOREIGN KEY (record_id) REFERENCES records (id)
);