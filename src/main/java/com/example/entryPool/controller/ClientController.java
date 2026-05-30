package com.example.entryPool.controller;

import com.example.entryPool.dto.request.CreateClientRequest;
import com.example.entryPool.dto.request.UpdateClientRequest;
import com.example.entryPool.dto.response.ClientResponse;
import com.example.entryPool.dto.response.ClientShortResponse;
import com.example.entryPool.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v0/pool")
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/clients")
    public List<ClientShortResponse> getClients(@RequestParam(required = false) String name) {
        log.info("Запрос на получение списка клиентов. Фильтр по имени: {}", name);
        return clientService.getClients(name);
    }

    @GetMapping("/clients/{id}")
    public ClientResponse getClient(@PathVariable Long id) {
        log.info("Запрос на получение списка клиентов. Фильтр по id: {}", id);
        return clientService.getClients(id);
    }

    @PostMapping("/clients")
    public void createClient(@RequestBody @Valid CreateClientRequest request) {
        log.info("Получен запрос на создание клиента: {}", request.name());
        clientService.createClient(request.name(), request.phone(), request.email());
    }

    @PatchMapping("/clients/{id}")
    public void updateClient(@RequestBody @Valid UpdateClientRequest request, @PathVariable Long id) {
        log.info("Запрос на обновление клиента. Фильтр по id: {}", id);
        clientService.updateClient(id, request.name(), request.phone(), request.email());
    }
}
