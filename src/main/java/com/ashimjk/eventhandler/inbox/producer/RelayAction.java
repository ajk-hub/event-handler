package com.ashimjk.eventhandler.inbox.producer;

import com.ashimjk.eventhandler.inbox.domain.Inbox;

@FunctionalInterface
public interface RelayAction {

    InboxRelay.Result onExecute(Inbox inbox);

}
