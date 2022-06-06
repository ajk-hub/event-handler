package com.ashimjk.eventhandler.outbox.producer.sender;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface OutboxEventSource {

    String OUTPUT = "outbox";

    @Output(OUTPUT)
    MessageChannel outbox();

}
