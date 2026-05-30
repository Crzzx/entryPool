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


    @Override
    public Optional<Schedule> getSchedule(LocalDate date) {
        String sql = "SELECT date, is_holiday, open_time, close_time FROM schedules WHERE date = ?";
        try {
            Schedule schedule = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Schedule s = new Schedule();
                s.setDate(rs.getDate("date").toLocalDate());
                s.setHoliday(rs.getBoolean("is_holiday"));

                Time openTime = rs.getTime("open_time");
                Time closeTime = rs.getTime("close_time");

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
