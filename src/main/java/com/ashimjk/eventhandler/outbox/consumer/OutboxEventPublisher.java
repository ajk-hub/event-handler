package com.ashimjk.eventhandler.outbox.consumer;

public interface OutboxEventPublisher {

    void fire(OutboxEvent outboxEvent);

}
