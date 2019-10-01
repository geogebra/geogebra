package org.geogebra.web.html5.gui.accessibility;

/**
 * Adapter for tabbing through multiple graphics views
 *
 * @author Zbynek
 */
public interface PerspectiveAccessibilityAdapter {

    /**
     * @param viewId current view ID
     * @return next graphics view ID
     */
    int nextID(int viewId);

    /**
     * @param viewId current view ID
     * @return previous graphics view ID
     */
    int prevID(int viewId);

    /**
     * @param viewId view ID
     * @return graphics view panel
     */
    EuclidianViewAccessibiliyAdapter getEuclidianPanel(int viewId);

    /**
     * @param viewId
     *            view ID
     * @return graphics view panel or null
     */
    EuclidianViewAccessibiliyAdapter getEVPanelWitZoomButtons(int viewId);

}
