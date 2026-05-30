--liquibase formatted sql

--changeset leo:1
CREATE TABLE clients
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    phone VARCHAR(20)  NOT NULL UNIQUE,
    email VARCHAR(255)
);

--changeset leo:2
CREATE TABLE reservations
(
    id               UUID PRIMARY KEY,
    client_id        INTEGER     NOT NULL REFERENCES clients (id),
    reservation_date DATE        NOT NULL,
    reservation_time TIME        NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    cancel_reason    TEXT,
    created_at       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP
);

--changeset leo:3
CREATE TABLE schedules
(
    date       DATE PRIMARY KEY,
    is_holiday BOOLEAN NOT NULL DEFAULT FALSE,
    open_time  TIME,
    close_time TIME
);

--changeset leo:4
CREATE INDEX idx_reservations_date ON reservations (reservation_date);
CREATE INDEX idx_reservations_client ON reservations (client_id);

CREATE INDEX idx_reservations_datetime ON reservations (reservation_date, reservation_time);