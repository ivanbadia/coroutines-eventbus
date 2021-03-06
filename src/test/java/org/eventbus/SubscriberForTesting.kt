package org.eventbus

abstract class SubscriberForTesting<EventType : DomainEvent> : DomainEventSubscriber<EventType> {
    val receivedEvents = mutableListOf<EventType>()
    override fun consume(event: EventType) {
        receivedEvents.add(event)
    }
}
