package com.ashimjk.eventhandler.inbox.producer.impl;

import com.ashimjk.eventhandler.inbox.domain.Inbox;
import com.ashimjk.eventhandler.inbox.producer.InboxProcessor;
import com.ashimjk.eventhandler.inbox.producer.InboxRelay;
import com.ashimjk.eventhandler.inbox.producer.RelayAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "event.inbox.useDefaultRelay", havingValue = "true", matchIfMissing = true)
public class DefaultRelayAction implements RelayAction {

    private final InboxProcessor inboxProcessor;

    @Override
    public InboxRelay.Result onExecute(Inbox inbox) {
        try {
            inboxProcessor.process(inbox.getEventData());
            return new InboxRelay.Result(inbox, true, null);
        } catch (Exception e) {
            LOGGER.warn("error processing inbox event ", e);
            return new InboxRelay.Result(inbox, false, e);
        }
    }

}
