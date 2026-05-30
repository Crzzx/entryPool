package com.example.entryPool.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;


public record CreateReservationRequest(
        @NotNull Long clientId,
        @NotEmpty List<String> datetime
) {
}
