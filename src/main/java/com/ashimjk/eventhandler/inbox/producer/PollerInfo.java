package com.ashimjk.eventhandler.inbox.producer;

import java.util.concurrent.atomic.AtomicBoolean;

public class PollerInfo {

    private final AtomicBoolean nextRun = new AtomicBoolean(false);

    public void start() {
        this.nextRun.set(true);
    }

    public boolean isRunning() {
        return this.nextRun.get();
    }

    public void stop() {
        this.nextRun.set(false);
    }

}
