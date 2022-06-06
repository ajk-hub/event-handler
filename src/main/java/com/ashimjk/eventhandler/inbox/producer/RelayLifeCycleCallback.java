package com.ashimjk.eventhandler.inbox.producer;

import com.ashimjk.eventhandler.inbox.domain.Inbox;

import java.util.List;

public interface RelayLifeCycleCallback {

    void onSuccess(List<Inbox> successList);

    void onFailure(List<RelayFailure> failureList);

}
