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
     * @param date time
     */
    CheatingEvent(CheatingAction action, Date date) {
        this.action = action;
        this.date = date;
    }

    public CheatingAction getAction() {
        return action;
    }

    public long getTime() {
        return date.getTime();
    }

    public Date getDate() {
        return date;
    }
}
