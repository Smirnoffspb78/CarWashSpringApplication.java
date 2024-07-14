--Услуги
CREATE TABLE IF NOT EXISTS services
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(250) NOT NULL UNIQUE,
    price NUMERIC      NOT NULL,
    time  INT
);

--Роли
CREATE TABLE IF NOT EXISTS roles
(
    name VARCHAR(250) NOT NULL PRIMARY KEY
);

--Пользователи
CREATE TABLE IF NOT EXISTS users
(
    id        SERIAL PRIMARY KEY,
    login     VARCHAR(250) NOT NULL,
    password  VARCHAR(250) NOT NULL,
    name      VARCHAR(200) NOT NULL,
    email     VARCHAR(200) NOT NULL,
    role_name VARCHAR(200) NOT NULL,
    discount  INT          NOT NULL DEFAULT 0,
    is_delete BOOLEAN      NOT NULL DEFAULT false,
    FOREIGN KEY (role_name) REFERENCES roles (name)
);

CREATE table IF NOT EXISTS employees
(
    id                    INT PRIMARY KEY,
    min_discount_for_user INT NOT NULL DEFAULT 0,
    max_discount_for_user INT NOT NULL DEFAULT 0,
    FOREIGN KEY (id) REFERENCES users (id)
);

--Боксы
CREATE TABLE IF NOT EXISTS boxes
(
    id          SERIAL PRIMARY KEY,
    start       TIME NOT NULL,
    finish      TIME NOT NULL,
    usage_rate  REAL NOT NULL,
    operator_id INT  NOT NULL,
    FOREIGN KEY (operator_id) REFERENCES users (id)
);

--Записи
CREATE TABLE IF NOT EXISTS records
(
    id          SERIAL PRIMARY KEY,
    user_id     INT       NOT NULL,
    start       TIMESTAMP NOT NULL,
    finish      TIMESTAMP NOT NULL,
    created     TIMESTAMP NOT NULL,
    is_reserve  BOOLEAN   NOT NULL DEFAULT false,
    is_complete BOOLEAN   NOT NULL DEFAULT false,
    is_remove   BOOLEAN   NOT NULL DEFAULT false,
    cost        NUMERIC   NOT NULL,
    box_id      INTEGER   NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (box_id) REFERENCES boxes (id)
);

--Связь услуг и записей
CREATE TABLE IF NOT EXISTS service_record
(
    service_id integer NOT NULL,
    record_id  integer NOT NULL,
    PRIMARY KEY (record_id, service_id),
    FOREIGN KEY (service_id) REFERENCES services (id),
    FOREIGN KEY (record_id) REFERENCES records (id)
);
