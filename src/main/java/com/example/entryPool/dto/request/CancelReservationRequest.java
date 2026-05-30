package com.example.entryPool.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CancelReservationRequest(
        @NotNull Long clientId,
        @NotBlank String reason
) {
}
