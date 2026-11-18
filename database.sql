DROP DATABASE IF EXISTS university_inventory;
CREATE DATABASE university_inventory CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE university_inventory;

CREATE TABLE asset (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    inv_number VARCHAR(40) NOT NULL UNIQUE,
    name VARCHAR(160) NOT NULL,
    category VARCHAR(100) NOT NULL,
    available_qty INT NOT NULL,
    criticality_score DOUBLE NOT NULL
);

CREATE TABLE app_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(160) NOT NULL,
    department VARCHAR(120) NOT NULL,
    role VARCHAR(60) NOT NULL,
    email VARCHAR(120) NOT NULL
);

CREATE TABLE issue_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    asset_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    requested_qty INT NOT NULL,
    urgency_score DOUBLE NOT NULL,
    state VARCHAR(30) NOT NULL,
    f1 DOUBLE NOT NULL,
    f2 DOUBLE NOT NULL,
    f3 DOUBLE NOT NULL,
    priority_index DOUBLE NOT NULL,
    FOREIGN KEY (asset_id) REFERENCES asset(id),
    FOREIGN KEY (user_id) REFERENCES app_user(id)
);

CREATE TABLE transfer_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    request_id BIGINT NOT NULL,
    action_type VARCHAR(40) NOT NULL,
    action_date VARCHAR(10) NOT NULL,
    condition_note VARCHAR(500) NOT NULL,
    FOREIGN KEY (request_id) REFERENCES issue_request(id)
);

INSERT INTO asset(inv_number, name, category, available_qty, criticality_score) VALUES
('INV-1001', 'Проєктор Epson EB-X49', 'Мультимедіа', 2, 80.0),
('INV-1002', 'Ноутбук Lenovo ThinkPad E15', 'Комп''ютерна техніка', 5, 65.0),
('INV-1003', 'Осцилограф цифровий Rigol DS1054Z', 'Лабораторне обладнання', 1, 95.0);

INSERT INTO app_user(full_name, department, role, email) VALUES
('Коваленко Олександр Іванович', 'Кафедра КІТС', 'Викладач', 'kovalenko@nupp.edu.ua'),
('Бондаренко Марія Сергіївна', 'Навчально-методичний відділ', 'Методист', 'bondarenko@nupp.edu.ua'),
('Мельник Андрій Петрович', 'Лабораторія мережевих технологій', 'Інженер', 'melnyk@nupp.edu.ua');

INSERT INTO issue_request(asset_id, user_id, requested_qty, urgency_score, state, f1, f2, f3, priority_index) VALUES
(1, 1, 1, 75.0, 'СТВОРЕНО', 75.0, 80.0, 50.0, 70.50),
(2, 2, 2, 40.0, 'СХВАЛЕНО', 40.0, 65.0, 40.0, 48.75);

INSERT INTO transfer_record(request_id, action_type, action_date, condition_note) VALUES
(2, 'ПЕРЕВІРКА КОМПЛЕКТНОСТІ', '2026-11-15', 'Комплектність перевірено, зауважень немає');
