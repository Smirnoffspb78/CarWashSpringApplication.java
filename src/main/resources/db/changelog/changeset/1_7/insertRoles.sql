--liquibase formatted sql

--changeset Dmitry Smirnov:1_7
--comment: Добавление ролей

--Роли
insert into roles(name)
values ('ROLE_USER'),
       ('ROLE_OPERATOR'),
       ('ROLE_ADMIN');