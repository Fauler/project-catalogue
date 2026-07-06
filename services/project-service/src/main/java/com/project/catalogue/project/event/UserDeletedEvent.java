package com.project.catalogue.project.event;

import java.time.Instant;

public record UserDeletedEvent(
        Long userId,
        String email,
        Instant occurredAt
) {
}
