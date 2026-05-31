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

/**
 * Сервис для управления данными клиентов.
 * Обеспечивает выполнение CRUD-операций и бизнес-валидацию при регистрации.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    /**
     * Возвращает список клиентов. Поддерживает фильтрацию по имени.
     *
     * @param name часть имени клиента для поиска; если null или пусто — возвращает всех клиентов
     * @return список {@link ClientShortResponse}
     */
    @Transactional
    @Override
    public List<ClientShortResponse> getClients(String name) {
        List<Client> clients = clientRepository.findAll(name);

        return clients.stream()
                .map(client -> new ClientShortResponse(client.getId(), client.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Ищет клиента по его уникальному идентификатору.
     *
     * @param id уникальный идентификатор клиента
     * @return детальная информация о клиенте {@link ClientResponse}
     * @throws ClientNotFoundException если клиент с таким ID не найден
     */
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

    /**
     * Регистрирует нового клиента. Проверяет уникальность номера телефона.
     *
     * @param name  ФИО клиента
     * @param phone номер телефона (должен быть уникальным)
     * @param email адрес электронной почты
     * @throws IllegalArgumentException если клиент с таким телефоном уже существует
     */
    @Transactional
    @Override
    public void createClient(String name, String phone, String email) {
        if (clientRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("Пользователь с таким номером уже существует");
        }
        clientRepository.createClient(name, phone, email);

        log.debug("Клиент {} успешно создан", name);
    }

    /**
     * Частично обновляет данные существующего клиента.
     * Если передаваемое значение параметра (name, phone, email) равно null, сохраняется старое значение.
     *
     * @param id    ID клиента для обновления
     * @param name  новое имя или null
     * @param phone новый телефон или null
     * @param email новый email или null
     * @throws ClientNotFoundException если клиент не найден
     * @throws IllegalArgumentException если обновление в БД не было выполнено
     */
    @Transactional
    @Override
    public void updateClient(Long id, String name, String phone, String email) {

        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Клиент не найден"));

        String newName = name != null ? name : existingClient.getName();
        String newPhone = phone != null ? phone : existingClient.getPhone();
        String newEmail = email != null ? email : existingClient.getEmail();

        boolean isUpdated = clientRepository.updateClient(id, newName, newPhone, newEmail);

        if (!isUpdated) {
            throw new IllegalArgumentException("Не удалось обновить данные клиента с ID " + id);
        }

        log.debug("Клиент {} успешно обновлен", id);
    }
}
