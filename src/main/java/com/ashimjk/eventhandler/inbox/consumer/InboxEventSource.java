package com.ashimjk.eventhandler.inbox.consumer;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface InboxEventSource {

    String INBOX_CHANNEL = "inboxChannel";

    @Input(INBOX_CHANNEL)
    SubscribableChannel inboxEvents();

    String FILTERED_EVENTS_CHANNEL = "filteredEventsChannel";

    @Input(FILTERED_EVENTS_CHANNEL)
    SubscribableChannel filteredInboxEvents();

}
