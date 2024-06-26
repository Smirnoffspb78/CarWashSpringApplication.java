/**Роли */
INSERT INTO roles(name)
VALUES ('USER'),
       ('OPERATOR'),
       ('ADMIN');

/*Тестовые пользователи*/
INSERT INTO users (name, login, password, email, role_name, discount,
                   is_delete)
VALUES ('Ivan', 'ivan', 'ivanivan', 'ivan@mail.ru', 'USER', 0, false),
       ('Petr', 'petr', 'petrpetr', 'petr@mail.ru', 'USER', 0, false),
       ('Alex', 'alex', 'alexalex', 'alex@mail.ru', 'USER', 0, false),
       ('Sidr', 'sidr', 'sidrsidr', 'sidr@mail.ru', 'USER', 0, false),
       ('Dmitry', 'dmitry', 'dmitrydmitry', 'dmitry@mail.ru', 'OPERATOR', 0, false),
       ('Admin', 'admin', 'adminadmin', 'admin@mail.ru', 'ADMIN', 0, false),
       ('Sergey', 'sergey', 'sergeysergey', 'sergey@mail.ru', 'OPERATOR', 0, false);


/*Тестовые скидки*/
INSERT INTO discount_workers (id, min_discount_for_user, max_discount_for_user)

VALUES(5, 0, 0),
      (6, 0, 0),
      (7, 0, 0);

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
INSERT INTO records(user_id, start, finish, is_reserve, is_complete, cost, box_id)
VALUES (1, '2024-05-17 12:00:00', '2024-05-17 12:40:00', false, true, 2000, 1),
       (2, '2024-05-17 13:00:00', '2024-05-17 13:50:00', false, true, 3000, 1),
       (1, '2024-06-01 12:00:00', '2024-06-01 12:40:00', false, true, 2000, 2),
       (3, '2024-05-17 14:00:00', '2024-05-17 14:10:00', false, true, 500, 3),
       (3, '2024-05-18 14:00:00', '2024-05-18 14:10:00', false, true, 500, 4),
       (4, '2024-05-18 14:00:00', '2024-05-18 14:10:00', false, false, 500, 1),
       (1, '2024-08-18 14:00:00', '2024-08-18 14:40:00', true, false, 3500, 1),
       (1, '2024-08-19 14:00:00', '2024-08-19 14:40:00', true, false, 3500, 1);

INSERT INTO service_record(service_id, record_id)
VALUES (5, 1),
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



