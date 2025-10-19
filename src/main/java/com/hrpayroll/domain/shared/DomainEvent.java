package com.hrpayroll.domain.shared;

import java.time.Instant;
import java.util.UUID;

/**
 * Base interface for all domain events in the system.
 * Domain events represent something that has happened in the domain.
 */
public interface DomainEvent {
    UUID getEventId();
    Instant getOccurredOn();
    String getEventType();
}
