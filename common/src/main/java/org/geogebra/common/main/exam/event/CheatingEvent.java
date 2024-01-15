package org.geogebra.common.main.exam.event;

/**
 * This class represents a cheating event.
 * A cheating event consists of a cheating action and the time of this action.
 */
public class CheatingEvent {

    private CheatingAction action;
    // TODO change to Date
    private Long time;

    /**
     * @param action action
     * @param time time
     */
    CheatingEvent(CheatingAction action, Long time) {
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
