package org.geogebra.common.gui.view.table;

/**
 * Creates points according to the table view model.
 */
public interface TableValuesPoints extends TableValuesListener {

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

    /**
     * Creates after data import table value points
     */
    void createAndAddPoints();
}
