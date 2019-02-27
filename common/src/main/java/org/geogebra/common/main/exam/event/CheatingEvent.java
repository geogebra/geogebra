package org.geogebra.common.main.exam.event;

public class CheatingEvent {

    private CheatingAction action;
    private Long time;

    public CheatingEvent(CheatingAction action, Long time) {
        this.action = action;
        this.time = time;
    }

    public CheatingAction getAction() {
        return action;
    }

    public Long getTime() {
        return time;
    }
}
