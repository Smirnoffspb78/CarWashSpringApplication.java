/*Тестовые пользователи*/
INSERT INTO users (name, login, password, email, role, discount, min_discount_for_user, max_discount_for_user,
                   is_delete)
VALUES ('Ivan', 'ivan', 'ivan', 'ivan@mail.ru', 'USER', 0, 0, 0, false),
       ('Petr', 'petr', 'petr', 'petr@mail.ru', 'USER', 0, 0, 0, false),
       ('Alex', 'alex', 'alex', 'alex@mail.ru', 'USER', 0, 0, 0, false),
       ('Sidr', 'sidr', 'sidr', 'sidr@mail.ru', 'USER', 0, 0, 0, false),
       ('Dmitry', 'dmitry', 'dmitry', 'dmitry@mail.ru', 'OPERATOR', 0, 0, 0, false),
       ('Admin', 'admin', 'admin', 'admin@mail.ru', 'ADMIN', 0, 0, 0, false),
       ('Sergey', 'sergey', 'sergey', 'sergey@mail.ru', 'OPERATOR', 0, 0, 0, false);


/*Тестовые боксы*/
INSERT INTO boxes (start, finish, usage_rate, operator_id)
VALUES ('8:00:00', '17:00:00', 1, 5),
       ('7:30:00', '16:30:00', 1.1, 5),
       ('8:30:00', '17:30:00', 1.2, 7),
       ('0:00:00', '23:59:59', 0.9, 7);

/*Тестовые услуги*/
INSERT INTO services(name, price, time)
VALUES ('Заливка охлаждающей жидкости', 500, 10),
       ('Чистка салона', 2000, 20),
       ('Мойка кузова', 1500, 30),
       ('Полировка фар', 1500, 20),
       ('Мойка двигателя паром', 1000, 20),
       ('Мойка радиатора паром', 1000, 20);


/*Тестовые записи */

INSERT INTO records(user_id, start, finish, is_reserve, is_complite, cost, box_id)
VALUES (1, '2024-05-17 12:00:00', '2024-05-17 12:40:00', true, true, 2000, 1),
       (2, '2024-05-17 13:00:00', '2024-05-17 13:50:00', true, true, 3000, 1),
       (1, '2024-06-01 12:00:00', '2024-06-01 12:40:00', true, true, 2000, 2),
       (3, '2024-05-17 14:00:00', '2024-05-17 14:10:00', true, true, 500, 3),
       (3, '2024-05-18 14:00:00', '2024-05-18 14:10:00', true, true, 500, 4),
       (4, '2024-05-18 14:00:00', '2024-05-18 14:10:00', false, false, 500, 1);

INSERT INTO service_record(service_id, record_id)
VALUES (5, 1),
       (6, 1),
       (3, 2),
       (4, 2),
       (5, 3),
       (6, 3),
       (1, 4),
       (1, 5),
       (1, 6);



