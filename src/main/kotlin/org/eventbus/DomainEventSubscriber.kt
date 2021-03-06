package org.eventbus

interface DomainEventSubscriber<EventType : DomainEvent> {
    fun consume(event: EventType)
}
