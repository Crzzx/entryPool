package com.example.entryPool.repository;

import com.example.entryPool.model.Client;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository {
    List<Client> findAll(String name);

    Optional<Client> findById(Long id);

    void createClient(String name, String phone, String email);

    boolean updateClient(Long id, String name, String phone, String email);

    public boolean existsByPhone(String phone);
}
