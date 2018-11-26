package org.geogebra.common.gui.view.algebra;

import java.util.Collections;
import java.util.TreeSet;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;

/**
 * Delegate that handles AV visibility
 */

public class AlgebraViewVisibilityDelegate {

    private boolean isViewVisible;
    private boolean updateOccurred, clearOccured;

    private TreeSet<GeoElement> geosToAdd;
    private TreeSet<GeoElement> geosToRemove;

	/**
	 * constructor
	 */
    public AlgebraViewVisibilityDelegate(App app) {
        geosToAdd = new TreeSet<>();
        if (app.has(Feature.G3D_IOS_FASTER_AV)) {
            geosToRemove = new TreeSet<>(Collections.reverseOrder());
        } else {
            geosToRemove = new TreeSet<>();
        }
    }

	/**
	 * Delegate will cache the update if view is not ready to process it
	 * 
	 * @return true if view can update
	 */
    public boolean shouldViewUpdate() {
        if (isViewVisible) {
            return true;
        }
        updateOccurred = true;
        return false;
    }

	/**
	 * Delegate will cache the add if view is not ready to process it
	 * 
	 * @param geo
	 *            geo to add
	 * @return true if view can add
	 */
    public boolean shouldViewAdd(GeoElement geo) {
        if (isViewVisible) {
            return true;
        }
        geosToAdd.add(geo);
        return false;
    }

	/**
	 * Delegate will cache the remove if view is not ready to process it
	 * 
	 * @param geo
	 *            geo to remove
	 * @return true if view can remove
	 */
    public boolean shouldViewRemove(GeoElement geo) {
        if (isViewVisible) {
            return true;
        }
        if (!geosToAdd.remove(geo)) {
            geosToRemove.add(geo);
        }
        return false;
    }

	/**
	 * Delegate will cache the clear if view is not ready to process it
	 * 
	 * @return true if view can clear
	 */
    public boolean shouldViewClear() {
        if (isViewVisible) {
            return true;
        }
        geosToAdd.clear();
        geosToRemove.clear();
        clearOccured = true;
        return false;
    }

	/**
	 * Tells the delegate the view is hidden.
	 */
    public void onViewHidden() {
        isViewVisible = false;
    }

	/**
	 * Tells the delegate the view is shown.
	 * 
	 * @param view
	 *            view to perform cached actions on
	 */
    public void onViewShown(AlgebraView view) {
        isViewVisible = true;
        if (clearOccured) {
            view.clearView();
            clearOccured = false;
        }
        for (GeoElement geo: geosToRemove) {
            view.remove(geo);
        }
        geosToRemove.clear();
        for (GeoElement geo: geosToAdd) {
            view.add(geo);
        }
        geosToAdd.clear();
        if (updateOccurred) {
            view.repaintView();
            updateOccurred = false;
        }
    }

	/**
	 * 
	 * @return true if some action needs a repaint
	 */
    public boolean wantsViewToRepaint() {
        return updateOccurred || clearOccured || !geosToRemove.isEmpty() || !geosToAdd.isEmpty();
    }
}
