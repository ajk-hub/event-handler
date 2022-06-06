package com.ashimjk.eventhandler.outbox.producer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "event.outbox")
public class OutboxProperties {

    private int maxEventsPerPolling = 20;
    private boolean useDefaultBinder = true;
    private long pollingIntervalInMilliseconds = 10000;

}
