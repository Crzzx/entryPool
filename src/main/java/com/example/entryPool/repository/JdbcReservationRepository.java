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

    public Map<LocalTime, Integer> getBookedCountByDate(LocalDate date) {
        String sql = "SELECT reservation_time, COUNT(*) as booked_count " +
                "FROM reservations WHERE reservation_date = ? " +
                "GROUP BY reservation_time";

        return jdbcTemplate.query(sql, rs -> {
            Map<LocalTime, Integer> map = new HashMap<>();
            while (rs.next()) {
                map.put(
                        rs.getTime("reservation_time").toLocalTime(),
                        rs.getInt("booked_count")
                );
            }
            return map;
        }, date);
    }

    public List<ReservationWithClient> getReservationsByDate(LocalDate date) {
        String sql = "SELECT r.id as reservation_id, r.reservation_date, r.reservation_time, " +
                "c.id as client_id, c.name, c.phone, c.email " +
                "FROM reservations r " +
                "JOIN clients c ON r.client_id = c.id " +
                "WHERE r.reservation_date = ? " +
                "ORDER BY r.reservation_time";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Client client = new Client(
                    rs.getLong("client_id"),
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("email")
            );

            Reservation reservation = new Reservation();
            reservation.setId(rs.getString("reservation_id"));
            reservation.setClientId(rs.getLong("client_id"));

            LocalDate resDate = rs.getDate("reservation_date").toLocalDate();
            LocalTime resTime = rs.getTime("reservation_time").toLocalTime();
            reservation.setReservationTime(LocalDateTime.of(resDate, resTime));

            reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
            reservation.setCancelReason(rs.getString("cancel_reason"));

            return new ReservationWithClient(reservation, client);
        }, date);
    }

    public Reservation createReservation(Long clientId, LocalDate date, LocalTime time){

        String id = UUID.randomUUID().toString();
        String defaultStatus = "ACTIVE";
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
        reservation.setStatus(ReservationStatus.valueOf(defaultStatus));
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

    public boolean deleteReservation(String reservationId, Long clientId, String reason) {
        String sql = "UPDATE reservations SET status = 'CANCELLED', cancel_reason = ? \" +\n" +
                "\"WHERE id = ? AND client_id = ? AND status = 'ACTIVE'";

        int rowsAffected = jdbcTemplate.update(sql, reason, reservationId, clientId);
        return rowsAffected > 0;
    }

}
