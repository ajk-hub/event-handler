package com.ashimjk.eventhandler.inbox.producer.impl;

import com.ashimjk.eventhandler.inbox.domain.Inbox;
import com.ashimjk.eventhandler.inbox.domain.InboxRepository;
import com.ashimjk.eventhandler.inbox.domain.State;
import com.ashimjk.eventhandler.inbox.producer.RelayFailure;
import com.ashimjk.eventhandler.inbox.producer.RelayLifeCycleCallback;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "event.inbox.useDefaultRelay", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class DefaultRelayLifeCycleCallback implements RelayLifeCycleCallback {

    private final InboxRepository repository;

    @Override
    public void onSuccess(List<Inbox> successList) {
        if (successList.isEmpty()) return;

        List<Inbox> entities = successList
                .stream()
                .peek(e -> e.setState(State.PROCESSED))
                .toList();

        repository.saveAll(entities);
    }

    @Override
    public void onFailure(List<RelayFailure> relayFailures) {
        if (relayFailures.isEmpty()) return;

        List<Inbox> entities = relayFailures
                .stream()
                .map(e -> {
                    Inbox inbox = e.inbox();
                    inbox.setState(State.FAILED);
                    inbox.addFailureReason(e.throwable().getMessage());
                    return inbox;
                })
                .toList();

        repository.saveAll(entities);
    }

}
