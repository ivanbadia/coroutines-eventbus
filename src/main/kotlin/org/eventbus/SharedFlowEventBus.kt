package org.eventbus

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.flow.*
import mu.KotlinLogging
import java.util.*

class SharedFlowEventBus : EventBus {
    private val _events = MutableSharedFlow<DomainEvent>()
    private val logger = KotlinLogging.logger {}
    val events: SharedFlow<DomainEvent> = _events.asSharedFlow()
    val scope = CoroutineScope(Default + CoroutineName("EventBus") + SupervisorJob())
    val subscribers: MutableList<DomainEventSubscriber<*>> =
        Collections.synchronizedList(mutableListOf<DomainEventSubscriber<*>>())

    override fun publish(event: DomainEvent) {
        scope.launch {
            _events.emit(event)
        }
    }

    inline fun <reified EventType : DomainEvent> subscribe(subscriber: DomainEventSubscriber<EventType>): SharedFlowEventBus {
        scope.launch {
            subscribers.add(subscriber)
            events
                .filter { event -> event is EventType }
                .map { it as EventType }
                .collect(sendEventTo(subscriber))
        }
        return this
    }

    fun <EventType : DomainEvent> sendEventTo(subscriber: DomainEventSubscriber<EventType>): suspend (value: EventType) -> Unit =
        { event ->
            try {
                subscriber.consume(event)
            } catch (exception: Exception) {
                logger.error(exception) { "Exception consuming event $event" }
            }
        }
}
