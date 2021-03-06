package org.eventbus

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime


class SharedFlowEventBusShould {
    private val eventBus = SharedFlowEventBus()
    private val orderCreatedSubscriber = OrderCreatedSubscriber()
    private val orderCancelledSubscriber = OrderCancelledSubscriber()

    @Test
    fun `publish events to subscribers`() {
        eventBus
            .subscribe(orderCreatedSubscriber)
            .subscribe(orderCancelledSubscriber)
        val orderCreated = OrderCreated()
        val orderCancelled = OrderCancelled()

        eventBus.publish(orderCreated)
        eventBus.publish(orderCancelled)

        assertThat(orderCreatedSubscriber.receivedEvents)
            .containsExactly(orderCreated)
        assertThat(orderCancelledSubscriber.receivedEvents)
            .containsExactly(orderCancelled)
    }
}

data class OrderCreated(override val occurredOn: OffsetDateTime = OffsetDateTime.now()) : DomainEvent

data class OrderCancelled(override  val occurredOn: OffsetDateTime = OffsetDateTime.now()) : DomainEvent

class OrderCreatedSubscriber : SubscriberForTesting<OrderCreated>()

class OrderCancelledSubscriber : SubscriberForTesting<OrderCancelled>()
