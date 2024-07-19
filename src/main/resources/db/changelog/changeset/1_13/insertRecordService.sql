--liquibase formatted sql

--changeset Dmitry Smirnov:1_13
--comment: Добавление связей записей и услуг

--Тестовые связи услуг и записей
insert into service_record(service_id, record_id)
values (5, 1),
       (6, 1),
       (3, 2),
       (4, 2),
       (5, 3),
       (6, 3),
       (1, 4),
       (1, 5),
       (1, 6),
       (2, 7),
       (4, 7),
       (2, 8),
       (4, 8);