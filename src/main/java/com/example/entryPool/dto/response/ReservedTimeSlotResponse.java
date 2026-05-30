package com.example.entryPool.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ReservedTimeSlotResponse(
        String time,
        int count,
        List<ClientResponse> clients
) {
}
