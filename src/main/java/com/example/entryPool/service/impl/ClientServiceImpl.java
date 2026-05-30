package com.example.entryPool.service.impl;

import com.example.entryPool.dto.response.ClientResponse;
import com.example.entryPool.dto.response.ClientShortResponse;
import com.example.entryPool.exception.ClientNotFoundException;
import com.example.entryPool.model.Client;
import com.example.entryPool.repository.ClientRepository;
import com.example.entryPool.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    @Override
    public List<ClientShortResponse> getClients(String name) {
        List<Client> clients = clientRepository.findAll(name);

        return clients.stream()
                .map(client -> new ClientShortResponse(client.getId(), client.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ClientResponse getClients(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Клиент с ID " + id + " не найден"));
        return ClientResponse.builder()
                .id(client.getId())
                .name(client.getName())
                .phone(client.getPhone())
                .email(client.getEmail())
                .build();
    }

    @Transactional
    @Override
    public void createClient(String name, String phone, String email) {
        if (clientRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("Пользователь с таким номером уже существует");
        }
        clientRepository.createClient(name, phone, email);

        log.info("Клиент {} успешно создан", name);
    }

    @Transactional
    @Override
    public void updateClient(Long id, String name, String phone, String email) {

        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Клиент не найден"));

        String newName = name != null ? name : existingClient.getName();
        String newPhone = phone != null ? phone : existingClient.getPhone();
        String newEmail = email != null ? email : existingClient.getEmail();

        clientRepository.updateClient(id, newName, newPhone, newEmail);

        log.info("Клиент {} успешно обновлен", id);
    }
}
