package com.ashimjk.eventhandler.inbox.producer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "event.inbox")
public class InboxProperties {

    private int maxEventsPerPolling = 20;
    private long pollingIntervalInMilliseconds = 10000;

}
