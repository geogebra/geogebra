package org.geogebra.common.util;

/**
 * Listens to timer events.
 */
public interface GTimerListener {
    /**
     * Called whenever the timer fires.
     */
    void onRun();
}