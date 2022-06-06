package com.ashimjk.eventhandler.outbox.producer;

import com.ashimjk.eventhandler.outbox.domain.Outbox;
import com.ashimjk.eventhandler.outbox.domain.OutboxRepository;
import com.ashimjk.eventhandler.outbox.producer.sender.EventSender;
import com.ashimjk.eventhandler.outbox.utils.OutboxUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "event.outbox.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxRelay {

    public static final String EVENT_TYPE_HEADER = "eventType";
    private final OutboxRepository outboxRepository;
    private final OutboxProperties properties;
    private final EventSender eventSender;

    @Transactional
    public int process() {
        PageRequest request = PageRequest.of(0, properties.getMaxEventsPerPolling());

        List<Result> results =
                outboxRepository
                        .findAllByOrderByOccurredOnAsc(request).stream()
                        .map(this::asyncSend)
                        .map(CompletableFuture::join)
                        .toList();

        List<Outbox> sentOutboxes =
                results.stream()
                       .filter(Result::succeed)
                       .map(Result::outbox)
                       .toList();

        outboxRepository.deleteAllInBatch(sentOutboxes);
        return sentOutboxes.size();
    }

    private CompletableFuture<Result> asyncSend(Outbox outbox) {
        return CompletableFuture.supplyAsync(() -> sendToQueue(outbox));
    }

    private Result sendToQueue(Outbox outbox) {
        try {
            String eventData = outbox.getEventData();
            Message<String> message = MessageBuilder
                    .withPayload(eventData)
                    .setHeader(EVENT_TYPE_HEADER, getEventType(eventData)).build();

            boolean ack = eventSender.sendEvent(message);
            return new Result(outbox, ack);

        } catch (Exception e) {
            LOGGER.error("Error on writing queue {}", outbox, e);
            return new Result(outbox, false);
        }
    }

    private String getEventType(String eventData) {
        return OutboxUtils.extractFromPayload("header.eventType", eventData);
    }

    private record Result(Outbox outbox, boolean succeed) {}

}
