package org.eventbus

class SharedFlowEventBus(subscriber: DomainEventSubscriber<*>) : EventBus {
    override fun publish(event: DomainEvent) {
        TODO("Not yet implemented")
    }

}
