CREATE TABLE IF NOT EXISTS services(
id SERIAL PRIMARY KEY,
name VARCHAR(250) NOT NULL,
price REAL NOT NULL,
time INT
);

CREATE TABLE IF NOT EXISTS accounts(
    id SERIAL PRIMARY KEY ,
    login VARCHAR(250) NOT NULL,
    password VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS users(
    id SERIAL PRIMARY KEY ,
    name VARCHAR(200) NOT NULL ,
    email VARCHAR(200) NOT NULL,
    role VARCHAR(200) NOT NULL ,
    discount REAL NOT NULL DEFAULT 0,
    is_delete BOOLEAN NOT NULL DEFAULT false,
    user_abstract VARCHAR(100),
    FOREIGN KEY (id) REFERENCES accounts(id)
);

CREATE TABLE IF NOT EXISTS boxes(
id SERIAL PRIMARY KEY,
start TIME NOT NULL,
finish TIME NOT NULL,
usage_rate REAL NOT NULL,
operator_id INT NOT NULL,
FOREIGN KEY (operator_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS service_box(
service_id INT NOT NULL,
box_id INT NOT NULL,
PRIMARY KEY (service_id, box_id),
FOREIGN KEY (service_id) REFERENCES services(id),
FOREIGN KEY (box_id) REFERENCES boxes(id)
);

CREATE TABLE IF NOT EXISTS records(
id SERIAL PRIMARY KEY ,
user_id INT NOT NULL,
start_date DATE NOT NULL,
start_time TIME NOT NULL,
finish_date DATE NOT NULL,
finish_time TIME NOT NULL,
is_reserve BOOLEAN NOT NULL DEFAULT true,
is_complite BOOLEAN NOT NULL DEFAULT false,
FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS service_record
(
    service_id integer NOT NULL,
    record_id integer NOT NULL,
    PRIMARY KEY (record_id, service_id),
    FOREIGN KEY (service_id) REFERENCES records (id) ,
    FOREIGN KEY (record_id) REFERENCES services (id)
)
