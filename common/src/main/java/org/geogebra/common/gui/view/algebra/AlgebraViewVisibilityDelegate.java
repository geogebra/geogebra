package org.geogebra.common.gui.view.algebra;

import java.util.Collections;
import java.util.TreeMap;
import java.util.TreeSet;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;

/**
 * Delegate that handles AV visibility
 */

public class AlgebraViewVisibilityDelegate {

    /**
     * Sorter to get geos in a given order related to AV view
     */
    public interface AlgebraViewSorter {

        static final public int FIRST_VALID_ROW = 0;

        /**
         *
         * Valid row is >= 0. Returning value < 0 is considered as invalid, and will be ignored
         *
         * @param geo geo element
         * @return row for sorting (e.g. row in AV view)
         */
        public int getRow(GeoElement geo);
    }

    private App app;

    private boolean isViewVisible;
    private boolean updateOccurred, clearOccured;

    private TreeSet<GeoElement> geosToAdd;
    private TreeSet<GeoElement> geosToRemove;

    /**
     * constructor
     */
    public AlgebraViewVisibilityDelegate(App app) {
        geosToAdd = new TreeSet<>();
        geosToRemove = new TreeSet<>();
        this.app = app;
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
        onViewShown(view, null);
    }

    /**
     * Tells the delegate the view is shown.
     *
     * @param view view to perform cached actions on
     * @param sorter sorter to process geos in the view order (e.g. row in AV)
     */
    public void onViewShown(AlgebraView view, AlgebraViewSorter sorter) {
        isViewVisible = true;
        if (clearOccured) {
            view.clearView();
            clearOccured = false;
        }
        if (!app.has(Feature.G3D_IOS_FASTER_AV) || sorter == null) {
            for (GeoElement geo : geosToRemove) {
                view.remove(geo);
            }
        } else {
            if (!geosToRemove.isEmpty()) {
                // ensure we'll remove from the last row to the first
                TreeMap<Integer, GeoElement> sortedSet =
                        new TreeMap<>(Collections.<Integer>reverseOrder());
                for (GeoElement geo : geosToRemove) {
                    int row = sorter.getRow(geo);
                    if (row >= AlgebraViewSorter.FIRST_VALID_ROW) {
                        sortedSet.put(row, geo);
                    }
                }
                for (GeoElement geo : sortedSet.values()) {
                    view.remove(geo);
                }
            }
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
