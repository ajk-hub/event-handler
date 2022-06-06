package com.ashimjk.eventhandler.inbox.producer;

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
@ConditionalOnProperty(name = "event.inbox.useDefaultScheduler", havingValue = "true", matchIfMissing = true)
public class InboxPoller {

    private final PollerInfo poller = new PollerInfo();
    private final InboxRelay inboxRelay;
    private final InboxProperties properties;

    @Scheduled(fixedDelayString = "${event.inbox.pollerFixedDelay:PT10s}")
    public void execute() {
        if (poller.isRunning()) return;

        poller.start();
        int countOfProcessedEvent = 0;
        Instant start = Instant.now();

        try {
            countOfProcessedEvent = inboxRelay.process();
        } catch (Exception e) {
            LOGGER.error("Error occurred on inbox relay.", e);
        } finally {
            poller.stop();
        }

        Instant finish = Instant.now();
        logInboxEventStatus(countOfProcessedEvent, start, finish);
    }

    private void logInboxEventStatus(int countOfProcessedEvent, Instant start, Instant finish) {
        if (countOfProcessedEvent > 0) {
            long timeElapsed = Duration.between(start, finish).toMillis();
            double avg = (double) timeElapsed / (double) countOfProcessedEvent;

            LOGGER.info(
                    "Successfully sent {} inboxes in {}ms with {} max events per polling.",
                    countOfProcessedEvent,
                    timeElapsed,
                    properties.getMaxEventsPerPolling()
            );
            LOGGER.info("Inbox relay rate [avg={}ms per inbox].", String.format("%.2f", avg));
        }
    }

}
