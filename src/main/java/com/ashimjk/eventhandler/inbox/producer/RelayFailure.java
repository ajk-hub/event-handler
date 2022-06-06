package com.ashimjk.eventhandler.inbox.producer;

import com.ashimjk.eventhandler.inbox.domain.Inbox;

public record RelayFailure(Inbox inbox, Throwable throwable) {
}
