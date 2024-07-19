--liquibase formatted sql

--changeset Dmitry Smirnov:1_8
--comment: Добавление пользователей

--Тестовые пользователи
insert into users (name, login, password, email, role_name, discount,
                   is_delete)
values ('Ivan', 'ivan', '$2a$12$Zz0lfws3bwmMFsa/mrpiEuwkJLD/yTpnWt8byL3eaeJrHgvqZCSRa', 'ivan@mail.ru', 'ROLE_USER', 0, false),
       ('Petr', 'petr', '$2a$12$IsCi37whu8cqGjZbUfTpKu1u9TWDCiGyQpabh4hiLXiPhwtKKTbf2', 'petr@mail.ru', 'ROLE_USER', 0, false),
       ('Alex', 'alex', '$2a$12$aH52755JHiMJKVQ8LRaw.eJEy/o549psZq8VGgAHeQfn2oh9qO5me', 'alex@mail.ru', 'ROLE_USER', 0, false),
       ('Sidr', 'sidr', '$2a$12$u7lsIqsCxrqxXYuv3QanlO5mn0PvL1RM1AbOhV6ezLbNEyYYQquce', 'sidr@mail.ru', 'ROLE_USER', 0, false),
       ('Dmitry', 'dmitry', '$2a$12$Tz9XZOkRsUOH.mU9iq0vPOo/5VBGinvW9z0QeeCmWHWeGVG0yHo/q', 'dmitry@mail.ru', 'ROLE_OPERATOR', 0, false),
       ('Admin', 'admin', '$2a$12$ldSbmPy0bBnD55oeztFkuuslHLZq5Iy3aRc8N2mw0QvYEezDW.6oa', 'admin@mail.ru', 'ROLE_ADMIN', 0, false),
       ('Sergey', 'sergey', '$2a$12$fXVh6iN5FINErr9CmiRI7e8kVVZBCnwdlc7xYAxTAKeUKvSumEhEi', 'sergey@mail.ru', 'ROLE_OPERATOR', 0, false);