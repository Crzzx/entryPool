package com.example.entryPool.dto.request;

import jakarta.validation.constraints.Email;

public record UpdateClientRequest(
        String name,
        String phone,
        @Email String email
) {
}
