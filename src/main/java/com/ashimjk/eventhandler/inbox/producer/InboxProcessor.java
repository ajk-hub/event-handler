package com.ashimjk.eventhandler.inbox.producer;

public interface InboxProcessor {

    void process(String eventPayload);

}
