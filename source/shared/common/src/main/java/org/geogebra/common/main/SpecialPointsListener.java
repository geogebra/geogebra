package org.geogebra.common.main;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;

public interface SpecialPointsListener {

    /**
     * Called when the special points have changed.
     *
     * @param manager the special points manager
     * @param specialPoints the new updated special points
     */
    void specialPointsChanged(SpecialPointsManager manager, List<GeoElement> specialPoints);
}
