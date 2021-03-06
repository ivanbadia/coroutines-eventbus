package org.eventbus

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

class SharedFlowEventBusShould {

    @Test
    fun `publish events to subscribers`() {
        val somethingHappened = SomethingHappened()
        val subscriberForTesting = SubscriberForTesting()

        SharedFlowEventBus(subscriberForTesting).publish(somethingHappened)

        assertThat(subscriberForTesting.events)
            .contains(somethingHappened)
    }


    class SomethingHappened : DomainEvent {
        private val occurredOn: OffsetDateTime = OffsetDateTime.now()
        override fun occurredOn(): OffsetDateTime {
            return occurredOn
        }

    }

    class SubscriberForTesting : DomainEventSubscriber<SomethingHappened> {
        val events = mutableListOf<SomethingHappened>()
        override fun consume(event: SomethingHappened) {
            events.add(event)
        }
    }
}
