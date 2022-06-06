package com.ashimjk.eventhandler.inbox.producer;

import com.ashimjk.eventhandler.inbox.domain.Inbox;
import com.ashimjk.eventhandler.inbox.domain.InboxRepository;
import com.ashimjk.eventhandler.inbox.domain.State;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "event.inbox.useDefaultRelay", havingValue = "true", matchIfMissing = true)
public class InboxRelay {

    private final RelayAction relayAction;
    private final InboxProperties properties;
    private final InboxRepository inboxRepository;
    private final RelayLifeCycleCallback relayLifeCycleCallback;

    @Transactional
    public int process() {
        PageRequest request = PageRequest.of(0, properties.getMaxEventsPerPolling());
        List<Result> results = inboxRepository
                .findAllByStateEqualsOrderByOccurredOnAsc(State.PENDING, request)
                .getContent()
                .stream()
                .map(this::asyncForward)
                .map(CompletableFuture::join)
                .toList();

        List<Inbox> successList = results
                .stream()
                .filter(Result::succeed)
                .map(Result::inbox)
                .toList();

        List<RelayFailure> failureList = results
                .stream()
                .filter(Result::isFailed)
                .map(result -> new RelayFailure(result.inbox(), result.throwable()))
                .toList();

        relayLifeCycleCallback.onSuccess(successList);
        relayLifeCycleCallback.onFailure(failureList);

        return successList.size();
    }

    protected CompletableFuture<Result> asyncForward(Inbox inbox) {
        return CompletableFuture.supplyAsync(() -> this.applyFunc(inbox));
    }

    protected Result applyFunc(Inbox inbox) {
        try {
            return relayAction.onExecute(inbox);
        } catch (Exception e) {
            LOGGER.warn("Error while processing inbox!", e);
            return new Result(inbox, false, e);
        }
    }

    public record Result(Inbox inbox, boolean succeed, Throwable throwable) {

        public boolean isFailed() {
            return !succeed();
        }

    }

}
