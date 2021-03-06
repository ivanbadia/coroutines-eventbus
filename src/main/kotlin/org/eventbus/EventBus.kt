package org.eventbus

interface EventBus {
    fun publish(event: DomainEvent)
}
