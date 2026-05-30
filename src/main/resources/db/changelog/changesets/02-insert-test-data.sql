--liquibase formatted sql

--changeset your_name:data-1
-- 1 января: праздник, бассейн закрыт полностью
INSERT INTO schedules (date, is_holiday, open_time, close_time)
VALUES ('2026-01-01', true, null, null);

-- 9 мая: короткий день (например, с 10:00 до 16:00)
INSERT INTO schedules (date, is_holiday, open_time, close_time)
VALUES ('2026-05-09', false, '10:00:00', '16:00:00');