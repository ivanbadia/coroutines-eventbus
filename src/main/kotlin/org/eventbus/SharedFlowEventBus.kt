package org.eventbus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SharedFlowEventBus : EventBus {
    private val _events = MutableSharedFlow<DomainEvent>()
    val events : SharedFlow<DomainEvent> = _events.asSharedFlow()
    private val scope = CoroutineScope(Default)

    override fun publish(event: DomainEvent) {
        scope.launch {
            println(Thread.currentThread().id)
            _events.emit(event)
        }
    }

    inline fun <reified T : DomainEvent> subscribe(subscriber: DomainEventSubscriber<T>): SharedFlowEventBus {
        GlobalScope.launch {
            events
                .filter { event -> event is T }
                .collect { event -> (subscriber as DomainEventSubscriber<DomainEvent>).consume(event) }
        }
        return this
    }

}
