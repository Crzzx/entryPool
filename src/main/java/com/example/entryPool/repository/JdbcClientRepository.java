package com.example.entryPool.repository;

import com.example.entryPool.model.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcClientRepository implements ClientRepository {
    private final JdbcTemplate jdbcTemplate;
    private final String ID = "id";
    private final String NAME = "name";
    private final String PHONE = "phone";
    private final String EMAIL = "email";

    @Override
    public Optional<Client> findById(Long id) {
        String sql = "SELECT id, name, phone, email FROM clients WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Client client = new Client();
                client.setId(rs.getLong(ID));
                client.setName(rs.getString(NAME));
                client.setPhone(rs.getString(PHONE));
                client.setEmail(rs.getString(EMAIL));
                return Optional.of(client);
            }, id);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Client> findAll(String name) {

        if (name == null || name.trim().isEmpty()) {
            String sql = "SELECT id, name FROM clients";
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Client client = new Client();
                client.setId(rs.getLong(ID));
                client.setName(rs.getString(NAME));
                return client;
            });
        } else {
            String sql = "SELECT id, name FROM clients WHERE name ILIKE ?";
            String searchParam = "%" + name + "%";

            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Client client = new Client();
                client.setId(rs.getLong(ID));
                client.setName(rs.getString(NAME));
                return client;
            }, searchParam);
        }
    }

    @Override
    public void createClient(String name, String phone, String email) {
        this.jdbcTemplate.update(
                "insert into clients (name, phone, email) values (?, ?, ?)",
                name, phone, email);
    }

    @Override
    public boolean updateClient(Long id, String name, String phone, String email) {
        int rows = this.jdbcTemplate.update(
                "update clients set name = ?, phone = ?, email = ? where id = ?",
                name, phone, email, id);
        return rows > 0;
    }

    @Override
    public boolean existsByPhone(String phone) {
        String sql = "SELECT count(*) FROM clients WHERE phone = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, phone);
        return count != null && count > 0;
    }
}
