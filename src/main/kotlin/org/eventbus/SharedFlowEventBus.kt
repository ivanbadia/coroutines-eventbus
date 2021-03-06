package org.eventbus

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class SharedFlowEventBus : EventBus {
    private val _events = MutableSharedFlow<DomainEvent>()
    private val events = _events.asSharedFlow()

    override fun publish(event: DomainEvent) {
        GlobalScope.launch {
            _events.emit(event)
        }
    }

    inline fun <reified T : DomainEvent> subscribe(subscriber: DomainEventSubscriber<T>): SharedFlowEventBus {
        registerSubscriber(subscriber, T::class.java)
        return this
    }

    fun <T : DomainEvent> registerSubscriber(subscriber: DomainEventSubscriber<T>, eventType: Class<T>) {
        GlobalScope.launch {
            events
                .filter { event ->
                    eventType == event.javaClass

                }
                .collect { event -> (subscriber as DomainEventSubscriber<DomainEvent>).consume(event) }
        }
    }
}
