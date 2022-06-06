package com.ashimjk.eventhandler.inbox.consumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = "eventId")
public class InboxHeader {

    private String eventId;
    private String eventType;
    private String eventTime;
    private String reference;
    private String process;
    private String tenantName;
    private String bankId;
    private String userName;
    private String sourceApp;
    private String sourceService;
    private String requestHostOrigin;

}
