package com.example.entryPool.repository;

import com.example.entryPool.dto.response.ClientResponse;
import com.example.entryPool.dto.response.ReservationWithClient;
import com.example.entryPool.dto.response.ReservedTimeSlotResponse;
import com.example.entryPool.model.Client;
import com.example.entryPool.model.Reservation;
import com.example.entryPool.model.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Repository
public class JdbcReservationRepository implements ReservationRepository{
    private final JdbcTemplate jdbcTemplate;
    private final String RESERVATION_TIME = "reservation_time";
    private final String BOOKED_COUNT = "booked_count";
    private final String CLIENT_ID = "client_id";
    private final String NAME = "name";
    private final String PHONE = "phone";
    private final String EMAIL = "email";
    private final String RESERVATION_ID = "reservation_id";
    private final String RESERVATION_DATE = "reservation_date";
    private final String STATUS = "status";
    private final String CANCEL_REASON = "cancel_reason";

    public Map<LocalTime, Integer> getBookedCountByDate(LocalDate date) {
        String sql = "SELECT reservation_time, COUNT(*) as booked_count " +
                "FROM reservations WHERE reservation_date = ? " +
                "GROUP BY reservation_time";

        return jdbcTemplate.query(sql, rs -> {
            Map<LocalTime, Integer> map = new HashMap<>();
            while (rs.next()) {
                map.put(
                        rs.getTime(RESERVATION_TIME).toLocalTime(),
                        rs.getInt(BOOKED_COUNT)
                );
            }
            return map;
        }, date);
    }

    public List<ReservationWithClient> getReservationsByDate(LocalDate date) {
        String sql = "SELECT r.id as reservation_id, r.reservation_date, r.reservation_time, " +
                "r.status, r.cancel_reason, " +
                "c.id as client_id, c.name, c.phone, c.email " +
                "FROM reservations r " +
                "JOIN clients c ON r.client_id = c.id " +
                "WHERE r.reservation_date = ? " +
                "ORDER BY r.reservation_time";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Client client = new Client(
                    rs.getLong(CLIENT_ID),
                    rs.getString(NAME),
                    rs.getString(PHONE),
                    rs.getString(EMAIL)
            );

            Reservation reservation = new Reservation();
            reservation.setId(rs.getObject(RESERVATION_ID, java.util.UUID.class));
            reservation.setClientId(rs.getLong(CLIENT_ID));

            java.sql.Date sqlDate = rs.getDate(RESERVATION_DATE);
            java.sql.Time sqlTime = rs.getTime(RESERVATION_TIME);

            if (sqlDate != null && sqlTime != null) {
                reservation.setReservationTime(LocalDateTime.of(sqlDate.toLocalDate(), sqlTime.toLocalTime()));
            }

            String statusStr = rs.getString(STATUS);
            if (statusStr != null) {
                reservation.setStatus(ReservationStatus.valueOf(statusStr));
            }

            reservation.setCancelReason(rs.getString(CANCEL_REASON));

            return new ReservationWithClient(reservation, client);
        }, java.sql.Date.valueOf(date));
    }

    public Reservation createReservation(Long clientId, LocalDate date, LocalTime time){
        UUID id = UUID.randomUUID();
        String defaultStatus = ReservationStatus.ACTIVE.name();

        String sql = "INSERT INTO reservations (id, client_id, reservation_date, reservation_time, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        this.jdbcTemplate.update(
                sql,
                id,
                clientId,
                date,
                time,
                defaultStatus
        );

        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setClientId(clientId);
        reservation.setReservationTime(LocalDateTime.of(date, time));
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setCancelReason(null);

        return reservation;
    }

    public int countReservations(LocalDate date, LocalTime time) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE reservation_date = ? AND reservation_time = ? AND status = 'ACTIVE'";
        return jdbcTemplate.queryForObject(sql, Integer.class, date, time);
    }

    public boolean existsByClientIdAndDate(Long clientId, LocalDate date) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE client_id = ? AND reservation_date = ? AND status = 'ACTIVE'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, clientId, date);
        return count != null && count > 0;
    }

    public boolean deleteReservation(UUID reservationId, Long clientId, String reason) {
        String sql = "UPDATE reservations SET status = 'CANCELLED', cancel_reason = ? " +
                "WHERE id = ? AND client_id = ? AND status = 'ACTIVE'";

        int rowsAffected = jdbcTemplate.update(sql, reason, reservationId, clientId);
        return rowsAffected > 0;
    }
}
