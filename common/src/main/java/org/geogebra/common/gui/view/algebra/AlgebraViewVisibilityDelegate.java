package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoElement;
import java.util.TreeSet;

/**
 * Delegate that handles AV visibility
 */

public class AlgebraViewVisibilityDelegate {

    private boolean isViewVisible;
    private boolean updateOccurred, clearOccured;

    private TreeSet<GeoElement> geosToAdd;
    private TreeSet<GeoElement> geosToRemove;

    public AlgebraViewVisibilityDelegate() {
        geosToAdd = new TreeSet<>();
        geosToRemove = new TreeSet<>();
    }

    public boolean viewShouldUpdate() {
        if (isViewVisible) {
            return true;
        }
        updateOccurred = true;
        return false;
    }

    public boolean viewShouldAdd(GeoElement geo) {
        if (isViewVisible) {
            return true;
        }
        geosToAdd.add(geo);
        return false;
    }

    public boolean viewShouldRemove(GeoElement geo) {
        if (isViewVisible) {
            return true;
        }
        if (!geosToAdd.remove(geo)) {
            geosToRemove.add(geo);
        }
        return false;
    }

    public boolean viewShouldClear() {
        if (isViewVisible) {
            return true;
        }
        geosToAdd.clear();
        geosToRemove.clear();
        clearOccured = true;
        return false;
    }

    public void onViewHidden() {
        isViewVisible = false;
    }

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

    public boolean didUpdateOccurred() {
        return updateOccurred;
    }
}
