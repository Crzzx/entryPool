package com.example.entryPool.repository;

import com.example.entryPool.model.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcScheduleRepository implements ScheduleRepository {
    private final JdbcTemplate jdbcTemplate;
    private final String DATE = "date";
    private final String IS_HOLIDAY = "is_holiday";
    private final String OPEN_TIME = "open_time";
    private final String CLOSE_TIME = "close_time";


    @Override
    public Optional<Schedule> getSchedule(LocalDate date) {
        String sql = "SELECT date, is_holiday, open_time, close_time FROM schedules WHERE date = ?";
        try {
            Schedule schedule = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Schedule s = new Schedule();
                s.setDate(rs.getDate(DATE).toLocalDate());
                s.setHoliday(rs.getBoolean(IS_HOLIDAY));

                Time openTime = rs.getTime(OPEN_TIME);
                Time closeTime = rs.getTime(CLOSE_TIME);

                s.setOpenTime(openTime != null ? openTime.toLocalTime() : null);
                s.setCloseTime(closeTime != null ? closeTime.toLocalTime() : null);

                return s;
            }, date);

            return Optional.ofNullable(schedule);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
