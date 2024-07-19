/**Роли */
insert into roles(name)
values ('ROLE_USER'),
       ('ROLE_OPERATOR'),
       ('ROLE_ADMIN');

/*Тестовые пользователи*/
insert into users (name, login, password, email, role_name, discount,
                   is_delete)
values ('Ivan', 'ivan', '$2a$12$Zz0lfws3bwmMFsa/mrpiEuwkJLD/yTpnWt8byL3eaeJrHgvqZCSRa', 'ivan@mail.ru', 'ROLE_USER', 0, false),
       ('Petr', 'petr', '$2a$12$IsCi37whu8cqGjZbUfTpKu1u9TWDCiGyQpabh4hiLXiPhwtKKTbf2', 'petr@mail.ru', 'ROLE_USER', 0, false),
       ('Alex', 'alex', '$2a$12$aH52755JHiMJKVQ8LRaw.eJEy/o549psZq8VGgAHeQfn2oh9qO5me', 'alex@mail.ru', 'ROLE_USER', 0, false),
       ('Sidr', 'sidr', '$2a$12$u7lsIqsCxrqxXYuv3QanlO5mn0PvL1RM1AbOhV6ezLbNEyYYQquce', 'sidr@mail.ru', 'ROLE_USER', 0, false),
       ('Dmitry', 'dmitry', '$2a$12$Tz9XZOkRsUOH.mU9iq0vPOo/5VBGinvW9z0QeeCmWHWeGVG0yHo/q', 'dmitry@mail.ru', 'ROLE_OPERATOR', 0, false),
       ('Admin', 'admin', '$2a$12$ldSbmPy0bBnD55oeztFkuuslHLZq5Iy3aRc8N2mw0QvYEezDW.6oa', 'admin@mail.ru', 'ROLE_ADMIN', 0, false),
       ('Sergey', 'sergey', '$2a$12$fXVh6iN5FINErr9CmiRI7e8kVVZBCnwdlc7xYAxTAKeUKvSumEhEi', 'sergey@mail.ru', 'ROLE_OPERATOR', 0, false);


/*Тестовые работники*/
INSERT INTO employees (id, min_discount_for_user, max_discount_for_user)

VALUES(5, 0, 0),
      (6, 0, 0),
      (7, 0, 0);

/*Тестовые боксы*/
insert into boxes (start, finish, usage_rate, operator_id)
VALUES ('8:00:00', '17:00:00', 1, 5),
       ('7:30:00', '16:30:00', 1.1, 5),
       ('8:30:00', '17:30:00', 1.2, 7),
       ('0:00:00', '23:59:59', 0.9, 7);

/*Тестовые услуги*/
insert into services(name, price, time)
values ('Заливка охлаждающей жидкости', 500, 10),
       ('Чистка салона', 2000, 20),
       ('Мойка кузова', 1500, 30),
       ('Полировка фар', 1500, 20),
       ('Мойка двигателя паром', 1000, 20),
       ('Мойка радиатора паром', 1000, 20);


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



