package org.geogebra.common.gui.view.table;

import org.geogebra.common.util.SuspenableListener;

/**
 * Creates points according to the table view model.
 */
public interface TableValuesPoints extends TableValuesListener, SuspenableListener {

    /**
     * Returns true if points are visible for this evaluatable.
     *
     * @param column column
     * @return true iff points are visible
     */
    boolean arePointsVisible(int column);

    /**
     * Sets if points should be visible for this evaluatable
     *
     * @param column column
     * @param visible visibility
     */
    void setPointsVisible(int column, boolean visible);
}
