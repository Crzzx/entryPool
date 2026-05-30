package com.example.entryPool.service.impl;

import com.example.entryPool.dto.response.ClientResponse;
import com.example.entryPool.dto.response.ClientShortResponse;
import com.example.entryPool.exception.ClientNotFoundException;
import com.example.entryPool.model.Client;
import com.example.entryPool.repository.ClientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    @DisplayName("Успешное получение всех клиентов")
    void getAllClients() {

        Client mockClient1 = new Client();
        mockClient1.setId(1L);
        mockClient1.setName("Иван");

        Client mockClient2 = new Client();
        mockClient2.setId(2L);
        mockClient2.setName("Петр");

        when(clientRepository.findAll(any())).thenReturn(List.of(mockClient1, mockClient2));

        List<ClientShortResponse> response = clientService.getClients("");

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals("Иван", response.get(0).name());
        assertEquals(1L, response.get(0).id());

        assertEquals("Петр", response.get(1).name());
        assertEquals(2L, response.get(1).id());
    }

    @Test
    @DisplayName("Успешное получение данных клиента по ID")
    void getClientById_Success() {

        Long clientId = 1L;
        Client mockClient = new Client();
        mockClient.setId(clientId);
        mockClient.setName("Иван");
        mockClient.setPhone("12345");
        mockClient.setEmail("ivan@test.com");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(mockClient));

        ClientResponse response = clientService.getClients(clientId);

        assertNotNull(response);
        assertEquals("Иван", response.name());
        assertEquals(clientId, response.id());
        verify(clientRepository, times(1)).findById(clientId); // Проверяем, что метод вызывался 1 раз
    }

    @Test
    @DisplayName("Ошибка получения клиента: клиент не найден")
    void getClientById_NotFound() {

        Long clientId = 99L;
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(ClientNotFoundException.class, () -> clientService.getClients(clientId));
    }

    @Test
    @DisplayName("Ошибка создания: клиент с таким телефоном уже существует")
    void createClient_AlreadyExists() {

        String phone = "79990001122";
        when(clientRepository.existsByPhone(phone)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                clientService.createClient("Имя", phone, "email@test.com")
        );

        verify(clientRepository, never()).createClient(any(), any(), any());
    }

    @Test
    @DisplayName("Обновление клиента: проверка сохранения старых данных при передаче null")
    void updateClient_MergeData() {

        Long clientId = 1L;
        Client existingClient = new Client();
        existingClient.setId(clientId);
        existingClient.setName("Старое Имя");
        existingClient.setPhone("Старый Телефон");
        existingClient.setEmail("old@test.com");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));

        clientService.updateClient(clientId, "Новое Имя", null, null);

        verify(clientRepository).updateClient(
                eq(clientId),
                eq("Новое Имя"),
                eq("Старый Телефон"),
                eq("old@test.com")
        );
    }
}