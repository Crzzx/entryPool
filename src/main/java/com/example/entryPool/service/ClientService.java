package com.example.entryPool.service;

import com.example.entryPool.dto.response.ClientResponse;
import com.example.entryPool.dto.response.ClientShortResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ClientService {
    List<ClientShortResponse> getClients(String name);

    ClientResponse getClients(Long id);

    void createClient(String name, String phone, String email);

    void updateClient(Long id, String name, String phone, String email);
}
