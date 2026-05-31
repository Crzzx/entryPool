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

/**
 * REST-контроллер для управления данными клиентов бассейна.
 * Предоставляет функционал для регистрации, поиска и обновления личной информации клиентов.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v0/pool")
public class ClientController {
    private final ClientService clientService;

    /**
     * Получает список клиентов бассейна.
     * Поддерживает фильтрацию по имени или части имени.
     *
     * @param name (опционально) имя клиента для поиска
     * @return список кратких данных клиентов {@link ClientShortResponse}
     */
    @GetMapping("/clients")
    public List<ClientShortResponse> getClients(@RequestParam(required = false) String name) {
        log.debug("Запрос на получение списка клиентов. Фильтр по имени: {}", name);
        return clientService.getClients(name);
    }

    /**
     * Получает полную информацию о конкретном клиенте по его идентификатору.
     *
     * @param id уникальный идентификатор клиента
     * @return детальная информация о клиенте {@link ClientResponse}
     * @throws com.example.entryPool.exception.ClientNotFoundException если клиент с таким ID не найден
     */
    @GetMapping("/clients/{id}")
    public ClientResponse getClient(@PathVariable Long id) {
        log.debug("Запрос на получение списка клиентов. Фильтр по id: {}", id);
        return clientService.getClients(id);
    }

    /**
     * Регистрирует нового клиента в системе бассейна.
     *
     * @param request объект {@link CreateClientRequest}, содержащий имя, телефон и email клиента
     */
    @PostMapping("/clients")
    public void createClient(@RequestBody @Valid CreateClientRequest request) {
        log.debug("Получен запрос на создание клиента: {}", request.name());
        clientService.createClient(request.name(), request.phone(), request.email());
    }

    /**
     * Выполняет частичное обновление данных существующего клиента.
     *
     * @param request объект {@link UpdateClientRequest} с новыми данными
     * @param id идентификатор клиента, данные которого необходимо обновить
     */
    @PatchMapping("/clients/{id}")
    public void updateClient(@RequestBody @Valid UpdateClientRequest request, @PathVariable Long id) {
        log.debug("Запрос на обновление клиента. Фильтр по id: {}", id);
        clientService.updateClient(id, request.name(), request.phone(), request.email());
    }
}
