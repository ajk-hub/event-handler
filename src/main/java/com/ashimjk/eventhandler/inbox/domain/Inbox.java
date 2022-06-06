package com.ashimjk.eventhandler.inbox.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Entity
@Table(name = "inbox")
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"eventData"})
public class Inbox implements Serializable {

    @Serial
    private static final long serialVersionUID = -4070991234426461594L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_Sequence")
    @SequenceGenerator(name = "id_Sequence", sequenceName = "id_sequence")
    private Long id;

    private LocalDateTime occurredOn;

    @Column(nullable = false, unique = true)
    private String eventId;

    private String eventType;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String eventData;

    @Enumerated(EnumType.STRING)
    private State state;

    private Integer retryCount;

    @Lob
    @ElementCollection
    @Column(name = "FAILURES")
    @CollectionTable(name = "INBOX_FAILURES", joinColumns = @JoinColumn(name = "INBOX_ID"))
    private List<String> failures = new ArrayList<>();

    @Transient
    public void addFailureReason(String failureReason) {
        if (failureReason == null || failureReason.isEmpty()) return;
        this.failures = Optional.ofNullable(this.failures).orElseGet(ArrayList::new);
        this.failures.add(failureReason);
    }

}
