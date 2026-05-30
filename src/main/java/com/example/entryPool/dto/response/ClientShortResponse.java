package com.example.entryPool.dto.response;

import lombok.Builder;

@Builder
public record ClientShortResponse(
        Long id,
        String name
) {
}
