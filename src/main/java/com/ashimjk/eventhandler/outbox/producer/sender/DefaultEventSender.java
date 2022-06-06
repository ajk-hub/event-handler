package com.ashimjk.eventhandler.outbox.producer.sender;

import org.springframework.messaging.Message;

public class DefaultEventSender implements EventSender {

    private final OutboxEventSource eventSource;

    public DefaultEventSender(OutboxEventSource eventSource) {
        this.eventSource = eventSource;
    }

    @Override
    public boolean sendEvent(Message<String> message) {
        return eventSource.outbox().send(message);
    }

}
