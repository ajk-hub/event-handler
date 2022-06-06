package com.ashimjk.eventhandler.inbox.consumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(of = "header")
public class InboxEvent {

    private InboxHeader header;
    private Object body;

}

