--liquibase formatted sql

--changeset Dmitry Smirnov:1_12
--comment: Добавление записей

/*Тестовые записи */
insert into records(user_id, start, finish, created, is_reserve, is_complete, cost, box_id)
values (1, '2024-05-17 12:00:00', '2024-05-17 12:40:00', '2023-05-17 10:00:00',true, true, 2000, 1),
       (2, '2024-05-17 13:00:00', '2024-05-17 13:50:00', '2023-05-17 11:00:00',true, true, 3000, 1),
       (1, '2024-06-01 12:00:00', '2024-06-01 12:40:00', '2023-06-01 10:00:00',true, true, 2000, 2),
       (3, '2024-05-17 14:00:00', '2024-05-17 14:10:00', '2023-05-17 12:00:00' ,true, true, 500, 3),
       (3, '2024-05-18 14:00:00', '2024-05-18 14:10:00', '2023-05-18 12:00:00' ,true, true, 500, 4),
       (4, '2024-05-18 14:00:00', '2024-05-18 14:10:00', '2023-05-18 12:00:00' ,false, false, 500, 1),
       (1, '2024-08-18 14:00:00', '2024-08-18 14:40:00', '2023-08-18 12:00:00',true, false, 3500, 1),
       (1, '2024-08-19 14:00:00', '2024-08-19 14:40:00', '2023-08-19 12:00:00',true, false, 3500, 1);