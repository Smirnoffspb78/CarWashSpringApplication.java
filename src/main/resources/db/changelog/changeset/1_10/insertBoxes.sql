--liquibase formatted sql

--changeset Dmitry Smirnov:1_10
--comment: Добавление боксов

/*Тестовые боксы*/
insert into boxes (start, finish, usage_rate, operator_id)
VALUES ('8:00:00', '17:00:00', 1, 5),
       ('7:30:00', '16:30:00', 1.1, 5),
       ('8:30:00', '17:30:00', 1.2, 7),
       ('0:00:00', '23:59:59', 0.9, 7);