package org.geogebra.common.euclidian;

/**
 * Recenters the Euclidian View after Clear all menu action and Algebra View size changes.
 */
public interface EvPositioner {

    /**
     * Recenters the Euclidian View on app start and on Clear all.
     */
    void reCenter();

    /**
     * Recenters the EV after Algebra View size changes.
     * @param avWidth av width
     * @param avHeight av height
     */
    void onAvSizeChanged(int avWidth, int avHeight);
}
