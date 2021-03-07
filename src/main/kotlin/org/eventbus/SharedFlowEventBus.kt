package org.eventbus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SharedFlowEventBus : EventBus {
    private val _events = MutableSharedFlow<DomainEvent>()
    val events : SharedFlow<DomainEvent> = _events.asSharedFlow()

    override fun publish(event: DomainEvent) {
        CoroutineScope(Default).launch {
            _events.emit(event)
        }
    }

    inline fun <reified EventType : DomainEvent> subscribe(subscriber: DomainEventSubscriber<EventType>): SharedFlowEventBus {
        CoroutineScope(Default).launch {
            events
                .filter { event -> event is EventType }
                .collect { event  -> subscriber.consume(event as EventType) }
        }
        return this
    }

}
