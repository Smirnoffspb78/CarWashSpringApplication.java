--liquibase formatted sql

--changeset Dmitry Smirnov:1_9
--comment: Добавление работников

/*Тестовые работники*/
INSERT INTO employees (id, min_discount_for_user, max_discount_for_user)

VALUES(5, 0, 0),
      (6, 0, 0),
      (7, 0, 0);