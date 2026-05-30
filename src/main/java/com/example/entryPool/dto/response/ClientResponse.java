package com.example.entryPool.dto.response;

import lombok.Builder;

@Builder
public record ClientResponse(
        Long id,
        String name,
        String phone,
        String email
) {
}
