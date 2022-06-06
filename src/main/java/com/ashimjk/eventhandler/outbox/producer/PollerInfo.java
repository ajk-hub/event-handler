package com.ashimjk.eventhandler.outbox.producer;

import java.util.concurrent.atomic.AtomicBoolean;

public class PollerInfo {

    private final AtomicBoolean runningStatus = new AtomicBoolean(false);

    protected void start() {
        this.runningStatus.set(true);
    }

    protected boolean isRunning() {
        return this.runningStatus.get();
    }

    protected void stop() {
        this.runningStatus.set(false);
    }

}
