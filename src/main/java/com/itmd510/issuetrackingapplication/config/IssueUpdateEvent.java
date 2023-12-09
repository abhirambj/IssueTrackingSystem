package com.itmd510.issuetrackingapplication.config;

import javafx.event.Event;
import javafx.event.EventType;

public class IssueUpdateEvent extends Event {
    public static final EventType<IssueUpdateEvent> ISSUE_UPDATE =
            new EventType<>(Event.ANY, "ISSUE_UPDATE");

    public IssueUpdateEvent() {
        super(ISSUE_UPDATE);
    }
}
