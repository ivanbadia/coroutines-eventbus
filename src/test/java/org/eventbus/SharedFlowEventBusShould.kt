package org.eventbus

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime


class SharedFlowEventBusShould {

    private val sharedFlowEventBus = SharedFlowEventBus()

    @Test
    fun `publish events to subscribers`() {
        val orderCreatedSubscriber = OrderCreatedSubscriber()
        val orderCancelledSubscriber = OrderCancelledSubscriber()
        sharedFlowEventBus.subscribe(orderCreatedSubscriber)
        sharedFlowEventBus.subscribe(orderCancelledSubscriber)
        val orderCreated = OrderCreated()
        val orderCancelled = OrderCancelled()

        sharedFlowEventBus.publish(orderCreated)
        sharedFlowEventBus.publish(orderCancelled)

        assertThat(orderCreatedSubscriber.events)
            .containsExactly(orderCreated)
        assertThat(orderCancelledSubscriber.events)
            .containsExactly(orderCancelled)
    }
    


    data class OrderCreated(private val occurredOn: OffsetDateTime = OffsetDateTime.now()) : DomainEvent {
        override fun occurredOn(): OffsetDateTime {
            return occurredOn
        }
    }

    data class OrderCancelled(private val occurredOn: OffsetDateTime = OffsetDateTime.now()) : DomainEvent {
        override fun occurredOn(): OffsetDateTime {
            return occurredOn
        }
    }

    abstract class SubscriberForTesting<T : DomainEvent> : DomainEventSubscriber<T> {
        val events = mutableListOf<T>()
        override fun consume(event: T) {
            events.add(event)
        }
    }

    class OrderCreatedSubscriber : SubscriberForTesting<OrderCreated>() {
        override fun subscribedTo(): Class<OrderCreated> = OrderCreated::class.java
    }

    class OrderCancelledSubscriber : SubscriberForTesting<OrderCancelled>(){
        override fun subscribedTo(): Class<OrderCancelled> = OrderCancelled::class.java
    }
}
