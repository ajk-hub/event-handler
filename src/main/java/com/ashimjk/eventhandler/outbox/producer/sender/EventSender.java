package com.ashimjk.eventhandler.outbox.producer.sender;

import org.springframework.messaging.Message;

public interface EventSender {

    boolean sendEvent(Message<String> message);

}
