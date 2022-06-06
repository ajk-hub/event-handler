package com.ashimjk.eventhandler.inbox.domain;

public enum State {
    PENDING,
    PROCESSED,
    FAILED,
    READY_TO_RETRY
}
