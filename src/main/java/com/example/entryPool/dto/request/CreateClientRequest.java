package com.example.entryPool.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateClientRequest(
        @NotBlank String name,
        @NotBlank String phone,
        @Email String email
) {
}
