package org.eventbus

import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit.SECONDS


class SharedFlowEventBusShould {
    private val orderCreatedSubscriber = OrderCreatedSubscriber()
    private val orderCancelledSubscriber = OrderCancelledSubscriber()
    private val errorSubscriber = SubscriberThatFails()
    private lateinit var eventBus : SharedFlowEventBus

    @BeforeEach
    internal fun setUp() {
        eventBus = SharedFlowEventBus()
            .subscribe(orderCreatedSubscriber)
            .subscribe(orderCancelledSubscriber)
            .subscribe(errorSubscriber)
        await
            .atMost(5, SECONDS)
            .until { eventBus.subscribers.size == 3 }
    }

    @Test
    fun `publish events to subscribers`() {
        val orderCreated = OrderCreated()
        val orderCancelled = OrderCancelled()

        eventBus.publish(orderCreated)
        eventBus.publish(orderCancelled)

        await
            .atMost(2, SECONDS)
            .untilAsserted {
                assertThat(orderCreatedSubscriber.receivedEvents)
                    .containsExactly(orderCreated)
                assertThat(orderCancelledSubscriber.receivedEvents)
                    .containsExactly(orderCancelled)
            }
    }


    @Test
    fun `publish events to other subscribers when one subscriber fails`() {
        eventBus.publish(EventForSubscriberThatFails())
        eventBus.publish(OrderCreated())
        eventBus.publish(EventForSubscriberThatFails())
        eventBus.publish(OrderCreated())

        await
            .atMost(2, SECONDS)
            .untilAsserted {
                assertThat(orderCreatedSubscriber.receivedEvents)
                    .hasSize(2)
            }
    }

}

data class OrderCreated(override val occurredOn: OffsetDateTime = OffsetDateTime.now()) : DomainEvent

data class OrderCancelled(override val occurredOn: OffsetDateTime = OffsetDateTime.now()) : DomainEvent

data class EventForSubscriberThatFails(override val occurredOn: OffsetDateTime = OffsetDateTime.now()) : DomainEvent

class OrderCreatedSubscriber : SubscriberForTesting<OrderCreated>()

class OrderCancelledSubscriber : SubscriberForTesting<OrderCancelled>()

class SubscriberThatFails : DomainEventSubscriber<EventForSubscriberThatFails> {
    override fun consume(event: EventForSubscriberThatFails) {
        throw RuntimeException()
    }
}
