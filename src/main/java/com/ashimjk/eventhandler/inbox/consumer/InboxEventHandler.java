package com.ashimjk.eventhandler.inbox.consumer;

import com.ashimjk.eventhandler.inbox.domain.Inbox;
import com.ashimjk.eventhandler.inbox.domain.InboxRepository;
import com.ashimjk.eventhandler.inbox.domain.State;
import com.ashimjk.eventhandler.inbox.utils.PayloadUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.annotation.Filter;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
public class InboxEventHandler {

    private final InboxRepository inboxRepository;
    private final InboxEventMap inboxEventMap;
    private final ObjectMapper objectMapper;
    private final boolean eventsFilteringEnabled;

    public InboxEventHandler(
            InboxRepository inboxRepository,
            InboxEventMap inboxEventMap,
            ObjectMapper objectMapper,
            @Value("${event.inbox.eventsFilteringEnabled:false}") boolean eventsFilteringEnabled
    ) {
        this.inboxRepository = inboxRepository;
        this.inboxEventMap = inboxEventMap;
        this.objectMapper = objectMapper;
        this.eventsFilteringEnabled = eventsFilteringEnabled;
    }

    @Filter(inputChannel = InboxEventSource.INBOX_CHANNEL,
            outputChannel = InboxEventSource.FILTERED_EVENTS_CHANNEL)
    public boolean filterRecord(@Payload InboxEvent event) {
        if (eventsFilteringEnabled) {
            InboxHeader header = event.getHeader();
            String eventType = header.getEventType();
            String service = header.getSourceService();
            boolean canProceed = inboxEventMap.isCbsEvent(service, eventType);
            LOGGER.debug("Consuming Inbox Event eventType '{}'  service '{}' reference '{}' canProceed '{}'",
                         event.getHeader().getEventType(),
                         event.getHeader().getSourceService(),
                         event.getHeader().getReference(),
                         canProceed);
            return canProceed;
        }
        return true;
    }

    @StreamListener(InboxEventSource.FILTERED_EVENTS_CHANNEL)
    public void onReceive(String payload) {
        try {
            InboxEvent event = PayloadUtils.toValue(objectMapper, payload, InboxEvent.class);
            if (isNewInboxEvent(event)) {
                InboxHeader header = event.getHeader();
                Inbox inbox = new Inbox();
                inbox.setEventData(payload);
                inbox.setEventId(header.getEventId());
                inbox.setEventType(header.getEventType());
                inbox.setOccurredOn(LocalDateTime.now());
                inbox.setState(State.PENDING);
                inboxRepository.save(inbox);
                LOGGER.info("saved event {}:{} with reference: {} for processing",
                            event.getHeader().getSourceService(),
                            event.getHeader().getEventType(),
                            event.getHeader().getReference());
            }
        } catch (JsonProcessingException e) {
            LOGGER.warn("could not parse incoming event to inbox-event structure ", e);
            Inbox inboxFailure = new Inbox();
            inboxFailure.addFailureReason(String.format("could not parse to inbox-event structure: %s",
                                                        e.getMessage()));
            inboxFailure.setEventData(payload);
            inboxFailure.setOccurredOn(LocalDateTime.now());
            inboxFailure.setState(State.FAILED);
            inboxRepository.save(inboxFailure);
        }
    }

    private boolean isNewInboxEvent(InboxEvent event) {
        return Optional.ofNullable(event)
                       .map(InboxEvent::getHeader)
                       .map(InboxHeader::getEventId)
                       .map(inboxRepository::existsByEventId)
                       .map(exists -> !exists)
                       .orElse(Boolean.FALSE);
    }

}
