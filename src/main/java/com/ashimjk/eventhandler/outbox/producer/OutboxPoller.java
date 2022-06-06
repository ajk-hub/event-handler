package com.ashimjk.eventhandler.outbox.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "event.outbox.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxPoller {

    private final PollerInfo poller = new PollerInfo();
    private final OutboxRelay outboxRelay;
    private final OutboxProperties properties;

    @Scheduled(fixedDelayString = "${event.outbox.polling-interval-in-milliseconds}")
    public void execute() {
        if (poller.isRunning()) return;

        poller.start();
        int countOfProcessedEvent = 0;
        Instant start = Instant.now();

        try {
            countOfProcessedEvent = outboxRelay.process();
        } catch (Exception e) {
            LOGGER.error("Error occurred on outbox relay.", e);
        } finally {
            poller.stop();
        }

        Instant finish = Instant.now();
        logOutboxEventStatus(countOfProcessedEvent, start, finish);
    }

    private void logOutboxEventStatus(int countOfProcessedEvent, Instant start, Instant finish) {
        if (countOfProcessedEvent > 0) {
            long timeElapsed = Duration.between(start, finish).toMillis();
            double avg = (double) timeElapsed / (double) countOfProcessedEvent;

            LOGGER.info(
                    "Successfully sent {} outboxes in {}ms with {} max events per polling.",
                    countOfProcessedEvent,
                    timeElapsed,
                    properties.getMaxEventsPerPolling()
            );
            LOGGER.info("Outbox relay rate [avg={}ms per outbox].", String.format("%.2f", avg));
        }
    }

}
