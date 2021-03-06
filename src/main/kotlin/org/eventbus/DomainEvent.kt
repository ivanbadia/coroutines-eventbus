package org.eventbus

import java.time.OffsetDateTime

interface DomainEvent {
    val occurredOn : OffsetDateTime
}
