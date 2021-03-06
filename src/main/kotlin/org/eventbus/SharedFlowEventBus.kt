package org.eventbus

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SharedFlowEventBus : EventBus {
    private val _events = MutableSharedFlow<DomainEvent>()
    private val events = _events.asSharedFlow()

    override fun publish(event: DomainEvent) {
        GlobalScope.launch {
            _events.emit(event)
        }
    }

    fun subscribe(subscriber: DomainEventSubscriber<*>) {
        GlobalScope.launch {
            events.collect { event -> (subscriber as DomainEventSubscriber<DomainEvent>).consume(event) }
        }
    }


}
