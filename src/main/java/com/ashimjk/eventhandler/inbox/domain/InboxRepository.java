package com.ashimjk.eventhandler.inbox.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

@Repository
public interface InboxRepository extends JpaRepository<Inbox, Long>, JpaSpecificationExecutor<Inbox> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Page<Inbox> findAllByStateEqualsOrderByOccurredOnAsc(State state, Pageable pageable);

    boolean existsByEventId(String eventId);

}
