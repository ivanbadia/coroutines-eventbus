package org.eventbus

import java.time.OffsetDateTime

interface DomainEvent {
    fun occurredOn(): OffsetDateTime
}
