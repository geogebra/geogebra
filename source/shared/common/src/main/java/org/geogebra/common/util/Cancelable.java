package org.geogebra.common.util;

/**
 * Action that can be canceled. Cancalation may not work on all platforms.
 */
public interface Cancelable {
    /**
     * Cancel this action.
     */
    void cancel();
}
