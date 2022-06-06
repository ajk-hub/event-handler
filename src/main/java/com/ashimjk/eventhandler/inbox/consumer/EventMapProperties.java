package com.ashimjk.eventhandler.inbox.consumer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "event.inbox.event-map")
public class EventMapProperties {

    private Map<String, String> service;

}
