package com.project.catalogue.user.event;

import java.time.Instant;

public record UserDeletedEvent(
        Long userId,
        String email,
        Instant occurredAt
) {
}
