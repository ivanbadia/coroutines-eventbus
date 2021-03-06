## Introduction

Simple implementation of an event bus using kotlin coroutines [shared flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-shared-flow/)

## How to Use

Create the event:

```
class OrderWasCreated (override val occurredOn: OffsetDateTime = OffsetDateTime.now()) : DomainEvent
```

Create the subscriber:

```
class OrderWasCreatedSubscriber : DomainEventSubscriber<OrderWasCreated> {
    override fun consume(event: OrderWasCreated) {
        println("Order created")
    }
}
```

Register the subscriber in the event bus:

```
val eventBus = SharedFlowEventBus()
eventBus.subscribe(OrderWasCreatedSubscriber())
```

Publish the event:

```
eventBus.publish(OrderWasCreated())
```
