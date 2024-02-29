package org.geogebra.common.main.exam.event;

import java.util.Date;

/**
 * This class represents a cheating event.
 * A cheating event consists of a cheating action and the time of this action.
 */
public final class CheatingEvent {

    private final CheatingAction action;
    private final Date date;

    /**
     * @param action action
     * @param time time
     */
    CheatingEvent(CheatingAction action, Long timemillis) {
        this.action = action;
        this.date = new Date(timemillis);
    }

    CheatingEvent(CheatingAction action, Date date) {
        this.action = action;
        this.date = date;
    }

    public CheatingAction getAction() {
        return action;
    }

    public Long getTime() {
        return date.getTime();
    }

    public Date getDate() {
        return date;
    }
}
