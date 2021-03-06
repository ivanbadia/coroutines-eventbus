package org.eventbus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class SharedFlowEventBus : EventBus {
    private val events = MutableSharedFlow<DomainEvent>()

    override fun publish(event: DomainEvent) {
        CoroutineScope(Unconfined).launch {
            events.emit(event)
        }
    }

    inline fun <reified T : DomainEvent> subscribe(subscriber: DomainEventSubscriber<T>): SharedFlowEventBus {
        registerSubscriber(subscriber, T::class.java)
        return this
    }

    fun <T : DomainEvent> registerSubscriber(subscriber: DomainEventSubscriber<T>, subscribedEventType: Class<T>) {
        GlobalScope.launch {
            events
                .filter(eventTypeIsEqualTo(subscribedEventType))
                .collect { event -> (subscriber as DomainEventSubscriber<DomainEvent>).consume(event) }
        }
    }

    private fun <T : DomainEvent> eventTypeIsEqualTo(eventType: Class<T>): suspend (DomainEvent) -> Boolean =
        { event -> eventType == event.javaClass }
}
