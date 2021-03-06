package org.eventbus

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime


class SharedFlowEventBusShould {

    private val sharedFlowEventBus = SharedFlowEventBus()

    @Test
    fun `publish events to subscribers`() {
            val subscriberForTesting = SubscriberForTesting()
            sharedFlowEventBus.subscribe(subscriberForTesting)
            val somethingHappened = SomethingHappened()

            sharedFlowEventBus.publish(somethingHappened)

            assertThat(subscriberForTesting.events)
                .contains(somethingHappened)
    }


    data class SomethingHappened(private val occurredOn: OffsetDateTime = OffsetDateTime.now()) : DomainEvent {
        override fun occurredOn(): OffsetDateTime {
            return occurredOn
        }

    }

    class SubscriberForTesting : DomainEventSubscriber<SomethingHappened> {
        val events = mutableListOf<DomainEvent>()
        override fun consume(event: SomethingHappened) {
            GlobalScope.launch {
                events.add(event)
            }
        }
    }
}
