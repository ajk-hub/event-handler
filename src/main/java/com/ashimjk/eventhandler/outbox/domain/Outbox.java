package com.ashimjk.eventhandler.outbox.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = Outbox.TABLE_NAME)
@Getter
@Setter
@EqualsAndHashCode(of = "outboxId")
@ToString(exclude = {"eventData"})
public class Outbox {

    public static final String TABLE_NAME = "outbox";
    public static final String OUTBOX_ID = "outbox_id";
    public static final String SORT_KEY = "occurred_on";
    public static final String EVENT_DATA = "event_data";

    @Id
    @Column(name = OUTBOX_ID)
    private String outboxId;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = EVENT_DATA, nullable = false, columnDefinition = "TEXT")
    private String eventData;

    @Column(name = SORT_KEY, nullable = false)
    private LocalDateTime occurredOn;

}
