package com.ashimjk.eventhandler.inbox.consumer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class InboxEventMap {

    private final EventMapProperties eventMapProperties;

    public boolean isCbsEvent(String service, String eventType) {
        return Stream.of(eventMapProperties.getService().getOrDefault(service, "").split(","))
                     .anyMatch(s -> s.equalsIgnoreCase(eventType));
    }

}
