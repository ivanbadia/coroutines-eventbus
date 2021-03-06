package org.eventbus

interface DomainEventSubscriber<EventType : DomainEvent> {
    fun consume(event: EventType)
    fun subscribedTo(): Class<EventType>
}
