package com.ashimjk.eventhandler.outbox.consumer;

import com.ashimjk.eventhandler.outbox.domain.Outbox;
import com.ashimjk.eventhandler.outbox.domain.OutboxRepository;
import com.ashimjk.eventhandler.outbox.utils.OutboxUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "event.outbox.enabled", havingValue = "true", matchIfMissing = true)
public class SpringOutboxEventPublisher implements OutboxEventPublisher {

    private static final Supplier<IllegalArgumentException> EMPTY_OUTBOX_EVENT =
            () -> new IllegalArgumentException("Empty outbox event.");

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void fire(OutboxEvent outboxEvent) {
        LOGGER.debug("Receive an outbox event {}.", outboxEvent);
        String eventData = OutboxUtils.toJsonString(objectMapper, outboxEvent)
                                      .orElseThrow(EMPTY_OUTBOX_EVENT);
        Outbox outbox = new Outbox();
        outbox.setOutboxId(UUID.randomUUID().toString());
        outbox.setEventData(eventData);
        outbox.setOccurredOn(LocalDateTime.now());
        this.outboxRepository.save(outbox);
        LOGGER.debug("Successfully saved an outbox event.");
    }

}
